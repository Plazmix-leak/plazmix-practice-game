package net.plazmix.practice.spleef.state;

import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.practice.spleef.scoreboard.SpleefIngameScoreboard;
import net.plazmix.practice.spleef.util.SpleefGameConstants;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpleefIngameState extends GameState {
    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(false)
            .setAllowInteraction(true)
            .addItem(1, ItemUtil.newBuilder(Material.DIAMOND_SPADE)
                    .setUnbreakable(true)
                    .addEnchantment(Enchantment.DIG_SPEED, 5)
                    .build())
            .build();

    public SpleefIngameState(GamePlugin plugin) {
        super(plugin, "Идет игра", false);
    }

    @Override
    protected void onStart() {
        GameSetting.PLAYER_DAMAGE.set(plugin.getService(), false);

        GameSetting.FOOD_CHANGE.set(plugin.getService(), false);

        GameSetting.BLOCK_BREAK.set(plugin.getService(), true);

        GameSetting.INTERACT_BLOCK.set(plugin.getService(), true);

        boolean teleportedToSpawn1 = false;

        // handle players.
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().setCollidesWithEntities(true);

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            player.sendTitle("§a§lИГРА НАЧАЛАСЬ", "");

            if(!teleportedToSpawn1) {
                player.teleport(plugin.getCache().getLocation(SpleefGameConstants.SPAWN_1));
                System.out.println("Teleported to Spawn1");
                teleportedToSpawn1 = true;
            } else {
                player.teleport(plugin.getCache().getLocation(SpleefGameConstants.SPAWN_2));
                System.out.println("Teleported to Spawn2");
            }

            player.setGameMode(GameMode.SURVIVAL);

            gameHotbar.setHotbarTo(player);

            new SpleefIngameScoreboard(player);
        }
    }

    @Override
    protected void onShutdown() {

    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (GameUser.from(player).isGhost()) {
            player.teleport(plugin.getService().getMapWorld().getSpawnLocation());
            return;
        }

        if (player.getLocation().getY() < plugin.getCache().getLocation(SpleefGameConstants.SPAWN_1).getY()) {

            GamePlugin.getInstance().getCache().set("winner", GameUser.from(
                    Bukkit.getOnlinePlayers().stream().filter(player1 -> !player1.getName().equalsIgnoreCase(player.getName())).findFirst().get()
            ));

            nextStage();
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
    public void onBreak(BlockBreakEvent event) {
        event.getBlock().setType(Material.AIR);
    }
}
