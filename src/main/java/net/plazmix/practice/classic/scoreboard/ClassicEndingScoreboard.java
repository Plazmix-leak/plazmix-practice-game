package net.plazmix.practice.classic.scoreboard;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.PlazmixPracticePlugin;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClassicEndingScoreboard {
    public ClassicEndingScoreboard(@NonNull Player player) {
        GameUser winnerUser = GamePlugin.getInstance().getCache().get("winner", GameUser.class);
        PlazmixPracticePlugin gamePlugin = (PlazmixPracticePlugin) PlazmixPracticePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay(gamePlugin.getPracticeMode().getColor() + gamePlugin.getPracticeMode().getTitle());
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(9, "Duels " + ChatColor.GRAY + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
        scoreboardBuilder.scoreboardLine(8, "");
        scoreboardBuilder.scoreboardLine(7, "§fПобедитель игры:");
        scoreboardBuilder.scoreboardLine(6, winnerUser.getPlazmixHandle().getDisplayName());
        scoreboardBuilder.scoreboardLine(5, "");
        scoreboardBuilder.scoreboardLine(4, "§fКарта: §a" + GamePlugin.getInstance().getService().getMapName());
        scoreboardBuilder.scoreboardLine(3, "§fРежим: §a" + GamePlugin.getInstance().getService().getServerMode());
        scoreboardBuilder.scoreboardLine(2, "§fСервер: §a" + PlazmixCoreApi.getCurrentServerName());
        scoreboardBuilder.scoreboardLine(1, "");
        scoreboardBuilder.scoreboardLine(0, "§dwww.plazmix.net");

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }
}
