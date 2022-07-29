package net.plazmix.practice.spleef.scoreboard;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.PlazmixPracticePlugin;
import net.plazmix.practice.mlgrush.util.MlgrushGameConstants;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpleefEndingScoreboard {
    public SpleefEndingScoreboard(@NonNull Player player) {
        GameUser winnerUser = GamePlugin.getInstance().getCache().get("winner", GameUser.class);
        PlazmixPracticePlugin gamePlugin = (PlazmixPracticePlugin) PlazmixPracticePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay(gamePlugin.getPracticeMode().getColor() + gamePlugin.getPracticeMode().getTitle());
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(7, "Duels " + ChatColor.GRAY + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
        scoreboardBuilder.scoreboardLine(6, "");
        scoreboardBuilder.scoreboardLine(5, "§fПобедитель игры:");
        scoreboardBuilder.scoreboardLine(4, winnerUser.getPlazmixHandle().getDisplayName());
        scoreboardBuilder.scoreboardLine(3, "");
        scoreboardBuilder.scoreboardLine(2, "§fРежим: §a" + GamePlugin.getInstance().getService().getServerMode());
        scoreboardBuilder.scoreboardLine(1, "");
        scoreboardBuilder.scoreboardLine(0, "§dwww.plazmix.net");

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }
}
