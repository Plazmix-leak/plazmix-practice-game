package net.plazmix.practice.mlgrush.state;

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
import net.plazmix.practice.general.CosmeticListener;
import net.plazmix.practice.mlgrush.mysql.MlgrushGameStatsMysqlDatabase;
import net.plazmix.practice.mlgrush.scoreboard.MlgrushEndingScoreboard;
import net.plazmix.practice.mlgrush.util.MlgrushGameConstants;
import net.plazmix.practice.sumo.util.SumoGameConstants;
import net.plazmix.practice.util.GeneralGameConstants;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Comparator;

public class MlgrushEndingState extends StandardEndingState {

    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(true)

            .addItem(5, ItemUtil.newBuilder(Material.PAPER)
                            .setName("§aСыграть еще раз")
                            .build(),

                    player -> GamePlugin.getInstance().getService().playAgain(player))

            .addItem(9, ItemUtil.newBuilder(Material.MAGMA_CREAM)
                            .setName("§aПокинуть арену")
                            .build(),

                    PlazmixCoreApi::redirectToLobby)

            .build();


    public MlgrushEndingState(GamePlugin plugin) {
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

        GameUser winnerUser = Bukkit.getOnlinePlayers()
                .stream()
                .filter(OfflinePlayer::isOnline)
                .map(GameUser::from)
                .max(Comparator.comparingInt(value -> value.getCache().getInt(MlgrushGameConstants.BEDS_BROKEN_DATA)))
                .orElse(null);

        if (winnerUser == null) {
            plugin.broadcastMessage(ChatColor.RED + "Произошли техничекие неполадки, из-за чего игра была принудительно остановлена!");

            forceShutdown();
            return;
        }

        // Add player win.
        int wins = PlazmixUser.of(winnerUser.getBukkitHandle()).getDatabaseValue("MLGRDuels", "Wins");
        System.out.println(wins);
        // Add player win.
        winnerUser.getCache().set(MlgrushGameConstants.WINS_PLAYER_DATA, wins + 1);

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

        GameMysqlDatabase statsMysqlDatabase = plugin.getService().getGameDatabase(MlgrushGameStatsMysqlDatabase.class);

        for (Player player : Bukkit.getOnlinePlayers()) {
            GameUser gameUser = GameUser.from(player);

            // Announcements.
            player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 2, 0);
            player.sendMessage(MlgrushGameConstants.PREFIX + "§aИгра окончена!");

            if (winnerUser.getName().equalsIgnoreCase(player.getName())) {
                player.sendTitle("§6§lПОБЕДА", "§fВы победили в этой дуэле!");

                player.sendMessage("§e+250 монет (победа)");
                gameUser.getPlazmixHandle().addCoins(250);

            } else {

                player.sendTitle(CosmeticListener.getLoseMessage(winnerUser), "§fВ этом дуэле победил " + winnerUser.getPlazmixHandle().getDisplayName());
            }

            // Player data insert
            int ingameKills = gameUser.getCache().getInt(MlgrushGameConstants.GAME_KILLS_DATA);

            gameUser.getCache().increment(MlgrushGameConstants.GAMES_PLAYED_PLAYER_DATA);
            gameUser.getCache().add(MlgrushGameConstants.KILLS_PLAYER_DATA, ingameKills);

            // Give rewards.
            int coins = (ingameKills * 10);

            player.sendMessage("§e+" + coins + " монет (" + NumberUtil.formattingSpaced(ingameKills, "убийство", "убийства", "убийств") + ")");
            gameUser.getPlazmixHandle().addCoins(coins);

            player.sendMessage("§3+5 опыта");
            gameUser.getPlazmixHandle().addExperience(5);

            // Set hotbar items.
            gameHotbar.setHotbarTo(player);

            // Update player data in database.
            statsMysqlDatabase.insert(false, GameUser.from(player));
        }
    }

    @Override
    protected void handleScoreboardSet(@NonNull Player player) {
        new MlgrushEndingScoreboard(player);
    }

    @Override
    protected Location getTeleportLocation() {
        return GeneralGameConstants.LOBBY_LOCATION;
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

}
