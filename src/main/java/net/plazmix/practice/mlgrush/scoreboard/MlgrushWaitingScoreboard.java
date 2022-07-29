package net.plazmix.practice.mlgrush.scoreboard;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.state.type.StandardWaitingState;
import net.plazmix.practice.PlazmixPracticePlugin;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MlgrushWaitingScoreboard {

    public MlgrushWaitingScoreboard(@NonNull StandardWaitingState.TimerStatus timerStatus, @NonNull Player player) {
        PlazmixPracticePlugin gamePlugin = (PlazmixPracticePlugin) PlazmixPracticePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay(gamePlugin.getPracticeMode().getColor() + gamePlugin.getPracticeMode().getTitle());
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(11, "Duels " + ChatColor.GRAY + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
        scoreboardBuilder.scoreboardLine(10, "");
        scoreboardBuilder.scoreboardLine(9, "§fКарта: §a" + gamePlugin.getService().getMapName());
        scoreboardBuilder.scoreboardLine(8, "§fИгроки: §a" + Bukkit.getOnlinePlayers().size() + "§f/§c" + gamePlugin.getService().getMaxPlayers());
        scoreboardBuilder.scoreboardLine(7, "");
        scoreboardBuilder.scoreboardLine(6, "§eОжидание оппонента...");
        scoreboardBuilder.scoreboardLine(5, "");
        scoreboardBuilder.scoreboardLine(4, "§fРежим: §a" + GamePlugin.getInstance().getService().getServerMode());
        scoreboardBuilder.scoreboardLine(2, "");
        scoreboardBuilder.scoreboardLine(1, "§dwww.plazmix.net");

        scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {
            baseScoreboard.updateScoreboardLine(8, player, "§fИгроки: §a" + Bukkit.getOnlinePlayers().size() + "§f/§c" + gamePlugin.getService().getMaxPlayers());
            baseScoreboard.updateScoreboardLine(6, player, (!timerStatus.isLived() ? "§eОжидание оппонента..." : "§fИгра начнется через §a" + NumberUtil.formattingSpaced(timerStatus.getLeftSeconds(), "§fсекунду", "§fсекунды", "§fсекунд")));

        }, 20);

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }

}
