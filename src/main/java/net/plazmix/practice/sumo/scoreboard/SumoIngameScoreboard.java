package net.plazmix.practice.sumo.scoreboard;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.practice.PlazmixPracticePlugin;
import net.plazmix.practice.sumo.listener.ComboListener;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SumoIngameScoreboard {

    public SumoIngameScoreboard(@NonNull Player player) {
        Player opponent = Bukkit.getOnlinePlayers().stream().filter(player1 -> !player1.getName().equalsIgnoreCase(player.getName())).findFirst().get();
        PlazmixPracticePlugin gamePlugin = (PlazmixPracticePlugin) PlazmixPracticePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay(gamePlugin.getPracticeMode().getColor() + gamePlugin.getPracticeMode().getTitle());
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(9, "Duels " + ChatColor.GRAY + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
        scoreboardBuilder.scoreboardLine(8, "");
        scoreboardBuilder.scoreboardLine(7, "§fОппонент: §b" + opponent.getName());
        scoreboardBuilder.scoreboardLine(6, "§fМаксимальное комбо: §c0");
        scoreboardBuilder.scoreboardLine(5, "");
        scoreboardBuilder.scoreboardLine(4, "§fКарта: §a" + GamePlugin.getInstance().getService().getMapName());
        scoreboardBuilder.scoreboardLine(3, "§fРежим: §a" + GamePlugin.getInstance().getService().getServerMode());
        scoreboardBuilder.scoreboardLine(2, "§fСервер: §a" + PlazmixCoreApi.getCurrentServerName());
        scoreboardBuilder.scoreboardLine(1, "");
        scoreboardBuilder.scoreboardLine(0, "§dwww.plazmix.net");

        scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {
            baseScoreboard.updateScoreboardLine(6, player1, "§fМаксимальное комбо: §c" + NumberUtil.spaced(ComboListener.PLAYERS_MAX_COMBO_MAP.getOrDefault(player.getName(), 0)));

        }, 5);

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }


}
