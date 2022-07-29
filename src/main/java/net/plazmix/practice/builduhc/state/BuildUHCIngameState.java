package net.plazmix.practice.builduhc.state;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.practice.builduhc.item.BuildUHCGoldenHeadItem;
import net.plazmix.practice.builduhc.scoreboard.BuildUHCIngameScoreboard;
import net.plazmix.practice.builduhc.util.BuildUHCGameConstants;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BuildUHCIngameState extends GameState {
    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(true)
            .setAllowInteraction(true)
            .addItem(10, ItemUtil.newBuilder(Material.ARROW)
                    .setAmount(16)
                    .build())
            .addItem(32, ItemUtil.newBuilder(Material.WATER_BUCKET)
                    .build())
            .addItem(31, ItemUtil.newBuilder(Material.LAVA_BUCKET)
                    .build())
            .addItem(9, ItemUtil.newBuilder(Material.WOOD)
                    .setAmount(64)
                    .build())
            .addItem(8, ItemUtil.newBuilder(new BuildUHCGoldenHeadItem("§e§lЗолотое яблоко", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDIxY2FiNDA5NWU3MWJkOTI1Y2Y0NjQ5OTBlMThlNDNhZGI3MjVkYjdjYzE3NWZkOWQxZGVjODIwOTE0YjNkZSJ9fX0=", new PotionEffect[] {new PotionEffect(PotionEffectType.ABSORPTION, (120 * 20) + 40, 4), new PotionEffect(PotionEffectType.REGENERATION, (20 * 20) + 40, 2)}).getActionItem().getItemStack())
                    .setAmount(2)
                    .build())
            .addItem(7, ItemUtil.newBuilder(Material.GOLDEN_APPLE)
                    .setAmount(6)
                    .build())
            .addItem(6, ItemUtil.newBuilder(Material.DIAMOND_AXE)
                    .setUnbreakable(true)
                    .build())
            .addItem(5, ItemUtil.newBuilder(Material.WATER_BUCKET)
                    .build())
            .addItem(4, ItemUtil.newBuilder(Material.LAVA_BUCKET)
                    .build())
            .addItem(3, ItemUtil.newBuilder(Material.BOW)
                    .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                    .setUnbreakable(true)
                    .build())
            .addItem(2, ItemUtil.newBuilder(Material.FISHING_ROD)
                    .setUnbreakable(true)
                    .build())
            .addItem(1, ItemUtil.newBuilder(Material.DIAMOND_SWORD)
                    .setUnbreakable(true)
                    .addEnchantment(Enchantment.DAMAGE_ALL, 3)
                    .build())
            .build();

    public BuildUHCIngameState(@NonNull GamePlugin plugin) {
        super(plugin, "Идёт игра", false);
    }

    @Override
    protected void onStart() {
        GameSetting.setAll(plugin.getService(), true);

        GameSetting.PLAYER_DROP_ITEM.set(plugin.getService(), false);

        GameSetting.BLOCK_BREAK.set(plugin.getService(), false);

        GameSetting.FOOD_CHANGE.set(plugin.getService(), false);

        boolean teleportedToSpawn1 = false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().setCollidesWithEntities(true);

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            player.sendTitle("§a§lИГРА НАЧАЛАСЬ", "");

            if(!teleportedToSpawn1) {
                player.teleport(plugin.getCache().getLocation(BuildUHCGameConstants.SPAWN_1));
                System.out.println("Teleported to Spawn1");
                teleportedToSpawn1 = true;
            } else {
                player.teleport(plugin.getCache().getLocation(BuildUHCGameConstants.SPAWN_2));
                System.out.println("Teleported to Spawn2");
            }
            player.setGameMode(GameMode.SURVIVAL);

            new BuildUHCIngameScoreboard(player);
            setPlayerDefaultInventory(player);
        }
    }

    @Override
    protected void onShutdown() {}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Создаем победителя и переходим к стадии завершения игры
        Player winnerPlayer = Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> !player1.getName().equalsIgnoreCase(player.getName()))
                .findFirst()
                .get();

        GamePlugin.getInstance().getCache().set("winner", GameUser.from(winnerPlayer));
        nextStage();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER || event.getDamager().getType() != EntityType.PLAYER) return;

        if(event.getFinalDamage() >= ((Player) event.getEntity()).getHealth()) {
            event.setCancelled(true);
            ((Player) event.getEntity()).getInventory().clear();

            GamePlugin.getInstance().getCache().set("winner", GameUser.from((Player) event.getDamager()));
            nextStage();
        }
    }

    public void setPlayerDefaultInventory(Player player) {
        player.getInventory().setBoots(ItemUtil.newBuilder(Material.DIAMOND_BOOTS)
                .setUnbreakable(true)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .build());

        player.getInventory().setLeggings(ItemUtil.newBuilder(Material.DIAMOND_LEGGINGS)
                .setUnbreakable(true)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .build());

        player.getInventory().setChestplate(ItemUtil.newBuilder(Material.DIAMOND_CHESTPLATE)
                .setUnbreakable(true)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .build());

        player.getInventory().setHelmet(ItemUtil.newBuilder(Material.DIAMOND_HELMET)
                .setUnbreakable(true)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .build());

        gameHotbar.setHotbarTo(player);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType() != Material.WOOD)
            event.setCancelled(true);
    }
}
