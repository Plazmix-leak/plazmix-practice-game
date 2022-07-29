package net.plazmix.practice.sumo.scoreboard;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
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

public class SumoWaitingScoreboard {

    public SumoWaitingScoreboard(@NonNull StandardWaitingState.TimerStatus timerStatus, @NonNull Player player) {
        PlazmixPracticePlugin gamePlugin = (PlazmixPracticePlugin) PlazmixPracticePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay(gamePlugin.getPracticeMode().getColor() + gamePlugin.getPracticeMode().getTitle());
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(9, "Duels " + ChatColor.GRAY + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
        scoreboardBuilder.scoreboardLine(8, "");
        scoreboardBuilder.scoreboardLine(7, "§fКарта: §a" + gamePlugin.getService().getMapName());
        scoreboardBuilder.scoreboardLine(6, "§fИгроки: §a" + Bukkit.getOnlinePlayers().size() + "§f/§c" + gamePlugin.getService().getMaxPlayers());
        scoreboardBuilder.scoreboardLine(5, "");
        scoreboardBuilder.scoreboardLine(4, "§eОжидание оппонента...");
        scoreboardBuilder.scoreboardLine(3, "");
        scoreboardBuilder.scoreboardLine(2, "§fРежим: §a" + GamePlugin.getInstance().getService().getServerMode());
        scoreboardBuilder.scoreboardLine(2, "§fСервер: §a" + PlazmixCoreApi.getCurrentServerName());
        scoreboardBuilder.scoreboardLine(1, "");
        scoreboardBuilder.scoreboardLine(0, "§dwww.plazmix.net");

        scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {
            baseScoreboard.updateScoreboardLine(6, player, "§fИгроки: §a" + Bukkit.getOnlinePlayers().size() + "§f/§c" + gamePlugin.getService().getMaxPlayers());
            baseScoreboard.updateScoreboardLine(4, player, (!timerStatus.isLived() ? "§eОжидание оппонента..." : "§fИгра начнется через §a" + NumberUtil.formattingSpaced(timerStatus.getLeftSeconds(), "§fсекунду", "§fсекунды", "§fсекунд")));

        }, 20);

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }

}

