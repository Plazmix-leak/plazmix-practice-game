package net.plazmix.practice.builduhc.state;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.type.StandardEndingState;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.game.utility.worldreset.GameWorldReset;
import net.plazmix.practice.PlazmixPracticePlugin;
import net.plazmix.practice.builduhc.mysql.BuildUHCGameStatsMysqlDatabase;
import net.plazmix.practice.builduhc.scoreboard.BuildUHCEndingScoreboard;
import net.plazmix.practice.builduhc.util.BuildUHCGameConstants;
import net.plazmix.practice.general.CosmeticListener;
import net.plazmix.practice.sumo.util.SumoGameConstants;
import net.plazmix.practice.util.GeneralGameConstants;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class BuildUHCEndingState extends StandardEndingState {
    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(true)

            .addItem(5, ItemUtil.newBuilder(Material.PAPER)
                            .setName("§aСыграть снова")
                            .build(),

                    player -> PlazmixPracticePlugin.getInstance().getService().playAgain(player))

            .addItem(9, ItemUtil.newBuilder(Material.MAGMA_CREAM)
                            .setName("§aПокинуть арену")
                            .build(),

                    PlazmixCoreApi::redirectToLobby)

            .build();

    public BuildUHCEndingState(@NonNull GamePlugin plugin) {
        super(plugin, "Перезагрузка");
    }

    @Override
    protected String getWinnerPlayerName() {
        return null;
    }

    @Override
    protected void handleStart() {
        GameSetting.setAll(plugin.getService(), false);

        GameWorldReset.resetAllWorlds();

        GameUser winnerUser = GamePlugin.getInstance().getCache().get("winner", GameUser.class);

        if (winnerUser == null) {
            plugin.broadcastMessage(ChatColor.RED + "Произошли техничекие неполадки, из-за чего игра была принудительно остановлена!");

            forceShutdown();
            return;
        }

        winnerUser.getBukkitHandle().getInventory().clear();

        int wins = PlazmixUser.of(winnerUser.getBukkitHandle()).getDatabaseValue("BuildUHC", "Wins");
        // Add player win.
        winnerUser.getCache().set(BuildUHCGameConstants.BUILDUHC_WINS_PLAYER_DATA, wins + 1);

        // Run fireworks spam.
        GameSchedulers.runTimer(0, 20, () -> {

            if (winnerUser.getBukkitHandle() == null) {
                return;
            }

            Firework firework = winnerUser.getBukkitHandle().getWorld().spawn(winnerUser.getBukkitHandle().getLocation(), Firework.class);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();

            fireworkMeta.setPower(1);
            fireworkMeta.addEffect(FireworkEffect.builder()
                    .with(FireworkEffect.Type.STAR)
                    .withColor(Color.RED)
                    .withColor(Color.GREEN)
                    .withColor(Color.WHITE)
                    .build());

            firework.setFireworkMeta(fireworkMeta);
        });

        GameMysqlDatabase statsMysqlDatabase = plugin.getService().getGameDatabase(BuildUHCGameStatsMysqlDatabase.class);

        for (Player player : Bukkit.getOnlinePlayers()) {

            // Announcements.
            player.playSound(player.getLocation(), Sound.ANVIL_USE, 2, 1);
            player.sendMessage(GeneralGameConstants.PREFIX + "§aИгра окончена!");

            GameUser user = GameUser.from(player);

            // Give rewards.
            if (winnerUser.getName().equalsIgnoreCase(player.getName())) {
                player.sendTitle("§6§lПОБЕДА", "§fВы победили в этой дуэли!");
                player.sendMessage(GeneralGameConstants.PREFIX + "§7+25 монет (победа)");
                player.sendMessage(GeneralGameConstants.PREFIX + "§d+50 опыта (победа)");
                GameUser.from(player).getPlazmixHandle().addCoins(25);
                GameUser.from(player).getPlazmixHandle().addExperience(50);

            } else {

                player.sendTitle(CosmeticListener.getLoseMessage(winnerUser), "§fВ этой дуэли победил " + winnerUser.getPlazmixHandle().getDisplayName());
                player.sendMessage(GeneralGameConstants.PREFIX + "§d+10 опыта (проигрыш)");
                GameUser.from(player).getPlazmixHandle().addExperience(10);
            }

            // Player data insert
            winnerUser.getCache().increment(BuildUHCGameConstants.BUILDUHC_GAMES_PLAYED_PLAYER_DATA);

            // Set hotbar items.
            gameHotbar.setHotbarTo(player);

            // Update player data in database.
            statsMysqlDatabase.insert(false, GameUser.from(player));
        }
    }

    @Override
    protected void handleScoreboardSet(@NonNull Player player) {
        new BuildUHCEndingScoreboard(player);
    }

    @Override
    protected Location getTeleportLocation() {
        return GeneralGameConstants.LOBBY_LOCATION;
    }


    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        if (event.getTo().getWorld().getSpawnLocation().getY() - event.getTo().getY() > 5) {

            Location teleportLocation = getTeleportLocation();

            if (teleportLocation != null) {
                event.getPlayer().teleport(teleportLocation);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}
