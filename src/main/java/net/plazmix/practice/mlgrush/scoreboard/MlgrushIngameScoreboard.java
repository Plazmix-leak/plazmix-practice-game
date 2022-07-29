package net.plazmix.practice.mlgrush.scoreboard;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.PlazmixPracticePlugin;
import net.plazmix.practice.mlgrush.util.MlgrushGameConstants;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MlgrushIngameScoreboard {

    public MlgrushIngameScoreboard(@NonNull Player player) {
        Player opponent = Bukkit.getOnlinePlayers().stream().filter(player1 -> !player1.getName().equalsIgnoreCase(player.getName())).findFirst().get();
        PlazmixPracticePlugin gamePlugin = (PlazmixPracticePlugin) PlazmixPracticePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        scoreboardBuilder.scoreboardDisplay(gamePlugin.getPracticeMode().getColor() + gamePlugin.getPracticeMode().getTitle());
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(14, ChatColor.GRAY + "Duels " + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
        scoreboardBuilder.scoreboardLine(13, "");
        scoreboardBuilder.scoreboardLine(12, " §a0 §f- §c0");
        scoreboardBuilder.scoreboardLine(11, "");
        scoreboardBuilder.scoreboardLine(10, "§7[Игровая статистика]:");
        scoreboardBuilder.scoreboardLine(9, " §fОппонент: §b" + opponent.getName());
        scoreboardBuilder.scoreboardLine(8, " §fУбийств: §a0");
        scoreboardBuilder.scoreboardLine(7, " §fНужно сломать: §a" + NumberUtil.formattingSpaced(MlgrushGameConstants.BEDS_COUNT, "кровать", "кровати", "кроватей"));
        scoreboardBuilder.scoreboardLine(6, "");
        scoreboardBuilder.scoreboardLine(5, "§fКарта: §a" + GamePlugin.getInstance().getService().getMapName());
        scoreboardBuilder.scoreboardLine(4, "§fРежим: §a" + GamePlugin.getInstance().getService().getServerMode());
        scoreboardBuilder.scoreboardLine(2, "");
        scoreboardBuilder.scoreboardLine(1, "§dwww.plazmix.net");

        scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {

            baseScoreboard.updateScoreboardLine(8, player, " §fУбийств: §e" + GameUser.from(player).getCache().getInt(MlgrushGameConstants.GAME_KILLS_DATA));
            baseScoreboard.updateScoreboardLine(12, player, " §a" + GameUser.from(player).getCache().getInt(MlgrushGameConstants.BEDS_BROKEN_DATA) + " §f- §c" + GameUser.from(opponent).getCache().getInt(MlgrushGameConstants.BEDS_BROKEN_DATA));

        }, 20);

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }
}
