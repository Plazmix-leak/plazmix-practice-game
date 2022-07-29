package net.plazmix.practice.sumo.state;

import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.sumo.listener.ComboListener;
import net.plazmix.practice.sumo.scoreboard.SumoIngameScoreboard;
import net.plazmix.practice.sumo.util.SumoGameConstants;
import net.plazmix.pvp.knockback.Knockback;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SumoIngameState extends GameState {

    public SumoIngameState(GamePlugin plugin) {
        super(plugin, "Идет игра", false);
    }

    @Override
    protected void onStart() {
        GameSetting.PLAYER_DAMAGE.set(plugin.getService(), true);

        GameSetting.INTERACT_BLOCK.set(plugin.getService(), false);

        boolean teleportedToSpawn1 = false;

        // handle players.
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().setCollidesWithEntities(true);

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            player.sendTitle("§a§lИГРА НАЧАЛАСЬ", "§fОткиньте соперника за пределы круга, чтобы выиграть!");

            if(!teleportedToSpawn1) {
                player.teleport(plugin.getCache().getLocation(SumoGameConstants.SPAWN_1));
                System.out.println("Teleported to Spawn1");
                teleportedToSpawn1 = true;
            } else {
                player.teleport(plugin.getCache().getLocation(SumoGameConstants.SPAWN_2));
                System.out.println("Teleported to Spawn2");
            }

            new SumoIngameScoreboard(player);
        }

        getPlugin().getServer().getPluginManager().registerEvents(new ComboListener(), getPlugin());
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

        if (player.getLocation().getY() < plugin.getCache().getLocation(SumoGameConstants.SPAWN_1).getY()) {

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
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        event.setDamage(0);

        GameUser gameUser = GameUser.from(event.getDamager().getName());
        gameUser.getCache().increment(SumoGameConstants.ATTACK_COUNT);
    }

}

