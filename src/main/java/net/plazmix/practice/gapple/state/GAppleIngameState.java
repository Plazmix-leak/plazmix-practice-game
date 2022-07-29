package net.plazmix.practice.gapple.state;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.practice.gapple.scoreboard.GAppleIngameScoreboard;
import net.plazmix.practice.gapple.util.GAppleGameConstants;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class GAppleIngameState extends GameState {
    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(true)
            .setAllowInteraction(true)
            .addItem(9, ItemUtil.newBuilder(Material.DIAMOND_BOOTS)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                    .addEnchantment(Enchantment.DURABILITY, 4)
                    .build())
            .addItem(8, ItemUtil.newBuilder(Material.DIAMOND_LEGGINGS)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                    .addEnchantment(Enchantment.DURABILITY, 4)
                    .build())
            .addItem(7, ItemUtil.newBuilder(Material.DIAMOND_CHESTPLATE)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                    .addEnchantment(Enchantment.DURABILITY, 4)
                    .build())
            .addItem(6, ItemUtil.newBuilder(Material.DIAMOND_HELMET)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                    .addEnchantment(Enchantment.DURABILITY, 4)
                    .build())
            .addItem(5, ItemUtil.newBuilder(new ItemStack(Material.POTION, 1, (short) 8258))
                    .build())
            .addItem(4, ItemUtil.newBuilder(new ItemStack(Material.POTION, 1, (short) 8258))
                    .build())
            .addItem(3, ItemUtil.newBuilder(new ItemStack(Material.POTION, 1, (short) 8258))
                    .build())
            .addItem(2, ItemUtil.newBuilder(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1))
                    .setAmount(32)
                    .build())
            .addItem(1, ItemUtil.newBuilder(Material.DIAMOND_SWORD)
                    .setUnbreakable(true)
                    .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                    .build())
            .build();

    public GAppleIngameState(@NonNull GamePlugin plugin) {
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
                player.teleport(plugin.getCache().getLocation(GAppleGameConstants.SPAWN_1));
                System.out.println("Teleported to Spawn1");
                teleportedToSpawn1 = true;
            } else {
                player.teleport(plugin.getCache().getLocation(GAppleGameConstants.SPAWN_2));
                System.out.println("Teleported to Spawn2");
            }

            new GAppleIngameScoreboard(player);
            setHotbar(player);
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

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER) return;

        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            event.setCancelled(true);
    }

    public void setHotbar(Player player) {
        player.getInventory().setBoots(ItemUtil.newBuilder(Material.DIAMOND_BOOTS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                .addEnchantment(Enchantment.DURABILITY, 4)
                .build());

        player.getInventory().setLeggings(ItemUtil.newBuilder(Material.DIAMOND_LEGGINGS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                .addEnchantment(Enchantment.DURABILITY, 4)
                .build());

        player.getInventory().setChestplate(ItemUtil.newBuilder(Material.DIAMOND_CHESTPLATE)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                .addEnchantment(Enchantment.DURABILITY, 4)
                .build());

        player.getInventory().setHelmet(ItemUtil.newBuilder(Material.DIAMOND_HELMET)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                .addEnchantment(Enchantment.DURABILITY, 4)
                .build());

        gameHotbar.setHotbarTo(player);
    }

    @EventHandler
    public void onIteamBreak(PlayerItemDamageEvent event) {
        event.setDamage(event.getDamage() * 5);
    }
}
