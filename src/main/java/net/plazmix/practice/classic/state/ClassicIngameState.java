package net.plazmix.practice.classic.state;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.practice.classic.scoreboard.ClassicIngameScoreboard;
import net.plazmix.practice.classic.util.ClassicGameConstants;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ClassicIngameState extends GameState {
    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(true)
            .setAllowInteraction(true)
            .addItem(4, ItemUtil.newBuilder(Material.ARROW)
                    .setAmount(8)
                    .build())
            .addItem(3, ItemUtil.newBuilder(Material.BOW)
                    .setUnbreakable(true)
                    .build())
            .addItem(2, ItemUtil.newBuilder(Material.FISHING_ROD)
                    .setUnbreakable(true)
                    .build())
            .addItem(1, ItemUtil.newBuilder(Material.IRON_SWORD)
                    .setUnbreakable(true)
                    .build())
            .build();

    public  ClassicIngameState(@NonNull GamePlugin plugin) {
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
                player.teleport(plugin.getCache().getLocation(ClassicGameConstants.SPAWN_1));
                System.out.println("Teleported to Spawn1");
                teleportedToSpawn1 = true;
            } else {
                player.teleport(plugin.getCache().getLocation(ClassicGameConstants.SPAWN_2));
                System.out.println("Teleported to Spawn2");
            }

            new ClassicIngameScoreboard(player);
            setHotbar(player);
        }
    }

    @Override
    protected void onShutdown() {}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

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

        if(event.getDamage() >= ((Player) event.getEntity()).getHealthScale()) {
            event.setCancelled(true);
            ((Player) event.getEntity()).getInventory().clear();

            GamePlugin.getInstance().getCache().set("winner", GameUser.from((Player) event.getDamager()));
            nextStage();
        }
    }

    public void setHotbar(Player player) {
        player.getInventory().setBoots(ItemUtil.newBuilder(Material.IRON_BOOTS)
                .setUnbreakable(true)
                .build());

        player.getInventory().setLeggings(ItemUtil.newBuilder(Material.IRON_LEGGINGS)
                .setUnbreakable(true)
                .build());

        player.getInventory().setChestplate(ItemUtil.newBuilder(Material.IRON_CHESTPLATE)
                .setUnbreakable(true)
                .build());

        player.getInventory().setHelmet(ItemUtil.newBuilder(Material.IRON_HELMET)
                .setUnbreakable(true)
                .build());

        gameHotbar.setHotbarTo(player);
    }
}
