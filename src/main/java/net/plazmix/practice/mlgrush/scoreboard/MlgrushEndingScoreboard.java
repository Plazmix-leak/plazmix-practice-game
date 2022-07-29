package net.plazmix.practice.mlgrush.scoreboard;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.PlazmixPracticePlugin;
import net.plazmix.practice.mlgrush.util.MlgrushGameConstants;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class MlgrushEndingScoreboard {

    public MlgrushEndingScoreboard(@NonNull Player player) {
        GameUser winnerUser = Bukkit.getOnlinePlayers()
                .stream()
                .filter(OfflinePlayer::isOnline)
                .map(GameUser::from)
                .max(Comparator.comparingInt(value -> value.getCache().getInt(MlgrushGameConstants.BEDS_BROKEN_DATA)))
                .orElse(GameUser.from(player));

        PlazmixPracticePlugin gamePlugin = (PlazmixPracticePlugin) PlazmixPracticePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay(gamePlugin.getPracticeMode().getColor() + gamePlugin.getPracticeMode().getTitle());
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(10, ChatColor.GRAY + "Duels " + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
        scoreboardBuilder.scoreboardLine(9, "");
        scoreboardBuilder.scoreboardLine(8, "§fПобедитель игры:");
        scoreboardBuilder.scoreboardLine(7, " " + winnerUser.getPlazmixHandle().getDisplayName());
        scoreboardBuilder.scoreboardLine(6, "");
        scoreboardBuilder.scoreboardLine(5, "§fКарта: §a" + GamePlugin.getInstance().getService().getMapName());
        scoreboardBuilder.scoreboardLine(4, "§fРежим: §a" + GamePlugin.getInstance().getService().getServerMode());
        scoreboardBuilder.scoreboardLine(2, "");
        scoreboardBuilder.scoreboardLine(1, "§dwww.plazmix.net");

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }

}
