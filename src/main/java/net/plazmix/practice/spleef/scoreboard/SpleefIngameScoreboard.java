package net.plazmix.practice.spleef.scoreboard;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.PlazmixPracticePlugin;
import net.plazmix.practice.sumo.listener.ComboListener;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpleefIngameScoreboard {
    public SpleefIngameScoreboard(@NonNull Player player) {
        Player opponent = Bukkit.getOnlinePlayers().stream().filter(player1 -> !player1.getName().equalsIgnoreCase(player.getName())).findFirst().get();
        PlazmixPracticePlugin gamePlugin = (PlazmixPracticePlugin) PlazmixPracticePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay(gamePlugin.getPracticeMode().getColor() + gamePlugin.getPracticeMode().getTitle());
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(8, "Duels " + ChatColor.GRAY + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
        scoreboardBuilder.scoreboardLine(7, "");
        scoreboardBuilder.scoreboardLine(6, "§fОппонент: §b" + GameUser.from(opponent).getPlazmixHandle().getDisplayName());
        scoreboardBuilder.scoreboardLine(5, "");
        scoreboardBuilder.scoreboardLine(4, "§fКарта: §a" + GamePlugin.getInstance().getService().getMapName());
        scoreboardBuilder.scoreboardLine(3, "§fРежим: §a" + GamePlugin.getInstance().getService().getServerMode());
        scoreboardBuilder.scoreboardLine(2, "§fСервер: §a" + PlazmixCoreApi.getCurrentServerName());
        scoreboardBuilder.scoreboardLine(1, "");
        scoreboardBuilder.scoreboardLine(0, "§dwww.plazmix.net");

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }
}
