package net.plazmix.practice.battlerush.scoreboard;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.PlazmixPracticePlugin;
import net.plazmix.practice.battlerush.util.BattleRushGameConstants;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BattleRushIngameScoreboard {
    public BattleRushIngameScoreboard(@NonNull Player player) {
        Player opponent = Bukkit.getOnlinePlayers().stream().filter(player1 -> !player1.getName().equalsIgnoreCase(player.getName())).findFirst().get();
        PlazmixPracticePlugin gamePlugin = (PlazmixPracticePlugin) PlazmixPracticePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay(gamePlugin.getPracticeMode().getColor() + gamePlugin.getPracticeMode().getTitle());
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(11, "Duels " + ChatColor.GRAY + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
        scoreboardBuilder.scoreboardLine(10, "");
        scoreboardBuilder.scoreboardLine(9, GameUser.from(player).getPlazmixHandle().getDisplayName() + "§r: "  + GameUser.from(player).getCache().getInt(BattleRushGameConstants.SCORE));
        scoreboardBuilder.scoreboardLine(8, GameUser.from(opponent).getPlazmixHandle().getDisplayName() + "§r: " + GameUser.from(opponent).getCache().getInt(BattleRushGameConstants.SCORE));
        scoreboardBuilder.scoreboardLine(7, "");
        scoreboardBuilder.scoreboardLine(6, "§e§lЦель: §aЗабить 5 раз");
        scoreboardBuilder.scoreboardLine(5, "");
        scoreboardBuilder.scoreboardLine(4, "§fКарта: §a" + GamePlugin.getInstance().getService().getMapName());
        scoreboardBuilder.scoreboardLine(3, "§fРежим: §a" + GamePlugin.getInstance().getService().getServerMode());
        scoreboardBuilder.scoreboardLine(2, "§fСервер: §a" + PlazmixCoreApi.getCurrentServerName());
        scoreboardBuilder.scoreboardLine(1, "");
        scoreboardBuilder.scoreboardLine(0, "§dwww.plazmix.net");

        scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {
            baseScoreboard.updateScoreboardLine(9, player1, GameUser.from(player1).getPlazmixHandle().getDisplayName() + "§r: "  + GameUser.from(player1).getCache().getInt(BattleRushGameConstants.SCORE));
            baseScoreboard.updateScoreboardLine(8, player1, GameUser.from(opponent).getPlazmixHandle().getDisplayName() + "§r: "  + GameUser.from(opponent).getCache().getInt(BattleRushGameConstants.SCORE));
        }, 20L);

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }
}
