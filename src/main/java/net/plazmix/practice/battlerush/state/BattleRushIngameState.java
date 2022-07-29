package net.plazmix.practice.battlerush.state;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.practice.battlerush.scoreboard.BattleRushIngameScoreboard;
import net.plazmix.practice.battlerush.util.BattleRushGameConstants;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.plazmix.PlazmixApi.newItemBuilder;

public class BattleRushIngameState extends GameState {
    private boolean teleportedToSpawn1 = false;
    private List<Block> placedBlocks = new ArrayList<>();

    public BattleRushIngameState(@NonNull GamePlugin plugin) {
        super(plugin, "Идёт игра", false);
    }

    @Override
    protected void onStart() {
        GameSetting.setAll(plugin.getService(), true);

        GameSetting.PLAYER_DROP_ITEM.set(plugin.getService(), false);

        GameSetting.FOOD_CHANGE.set(plugin.getService(), false);

        for(Player player : Bukkit.getOnlinePlayers()) {
            if (!teleportedToSpawn1) {
                GameUser.from(player).getCache().set(BattleRushGameConstants.PLAYER_SPAWN, plugin.getCache().getLocation(BattleRushGameConstants.SPAWN_BLUE));
                player.teleport(plugin.getCache().getLocation(BattleRushGameConstants.SPAWN_BLUE));
                System.out.println("Teleported to SpawnBlue");
                teleportedToSpawn1 = true;
            } else {
                GameUser.from(player).getCache().set(BattleRushGameConstants.PLAYER_SPAWN, plugin.getCache().getLocation(BattleRushGameConstants.SPAWN_RED));
                player.teleport(plugin.getCache().getLocation(BattleRushGameConstants.SPAWN_RED));
                System.out.println("Teleported to SpawnRed");
            }

            setPlayerHotbar(GameUser.from(player));

            player.setGameMode(GameMode.SURVIVAL);

            new BattleRushIngameScoreboard(player);
        }
    }

    @Override
    protected void onShutdown() {}

    public void resetPlayers(Player scoredPlayer) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(GameUser.from(player).getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN));
            player.sendTitle(GameUser.from(scoredPlayer).getPlazmixHandle().getDisplayName() + "§a§l прыгнул в портал!", "");
            setPlayerHotbar(GameUser.from(player));
        }
    }

    public void resetBlocks() {
        placedBlocks.forEach(block -> {
            block.setType(Material.AIR);
        });
    }

    public void setPlayerHotbar(GameUser user) {
        if(user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_BLUE)) > user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_RED))) {
            GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
                    .setMoveItems(true)
                    .setAllowInteraction(true)
                    .addItem(1, new ItemStack(Material.WOOL, 32, (short) 14))
                    .addItem(2, newItemBuilder(Material.SHEARS).setUnbreakable(true).build())
                    .build();
            gameHotbar.setHotbarTo(user.getBukkitHandle());
            System.out.println("Distance red: " + user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_RED)));
        } else if(user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_BLUE)) < user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_RED))) {
            GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
                    .setMoveItems(true)
                    .setAllowInteraction(true)
                    .addItem(1, new ItemStack(Material.WOOL, 32, (short) 11))
                    .addItem(2, newItemBuilder(Material.SHEARS).setUnbreakable(true).build())
                    .build();
            gameHotbar.setHotbarTo(user.getBukkitHandle());
            System.out.println("Distance blue: " + user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_BLUE)));
        }
        System.out.println("Distance red: " + user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_RED)));
        System.out.println("Distance blue: " + user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_BLUE)));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        GameUser user = GameUser.from(event.getPlayer());
        if(user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).getY() - event.getTo().getY() > 50) {
            event.getPlayer().teleport(user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN));
            setPlayerHotbar(user);
            return;
        }

        if(user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_BLUE)) > user.getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_RED))) {
            if(event.getPlayer().getLocation().distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_BLUE)) < 2 && event.getPlayer().getLocation().getY() == plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_BLUE).getY()) {
                resetPlayers(event.getPlayer());
                resetBlocks();
                setPlayerHotbar(user);
                user.getCache().increment(BattleRushGameConstants.SCORE);
                if(user.getCache().getInt(BattleRushGameConstants.SCORE) == 5) {
                    GamePlugin.getInstance().getCache().set("winner", user);
                    nextStage();
                }
            }
        } else {
            if(event.getPlayer().getLocation().distance(plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_RED)) < 2 && event.getPlayer().getLocation().getY() == plugin.getCache().getLocation(BattleRushGameConstants.PORTAL_RED).getY()) {
                resetPlayers(event.getPlayer());
                resetBlocks();
                setPlayerHotbar(user);
                user.getCache().increment(BattleRushGameConstants.SCORE);
                if(user.getCache().getInt(BattleRushGameConstants.SCORE) == 5) {
                    GamePlugin.getInstance().getCache().set("winner", user);
                    nextStage();
                }
            }
        }
    }

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
    public void onDamage(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER || event.getDamager().getType() != EntityType.PLAYER) return;

        event.setDamage(0);
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER || event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        event.setDamage(0);
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        placedBlocks.add(event.getBlock());
        if(placedBlocks.size() > 20) {
            placedBlocks.get(0).setType(Material.AIR);
            placedBlocks.remove(0);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!placedBlocks.contains(event.getBlock())) {
            event.setCancelled(true);
            return;
        }

        placedBlocks.remove(event.getBlock());
    }
}
