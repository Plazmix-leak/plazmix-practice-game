package net.plazmix.practice;

import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.practice.battlerush.BattleRushInstallerTask;
import net.plazmix.practice.builduhc.BuildUHCInstallerTask;
import net.plazmix.practice.classic.ClassicInstallerTask;
import net.plazmix.practice.gapple.GAppleInstallerTask;
import net.plazmix.practice.mlgrush.MlgrushInstallerTask;
import net.plazmix.practice.nodebuff.NoDebuffInstallerTask;
import net.plazmix.practice.spleef.SpleefInstallerTask;
import net.plazmix.practice.sumo.SumoInstallerTask;
import org.bukkit.Bukkit;

public class PlazmixPracticeLoader {

    public static GameInstallerTask loadPractice(GamePlugin gamePlugin) {
        switch (PlazmixPracticePlugin.getPracticeMode()) {
            case SUMO:
                return new SumoInstallerTask(gamePlugin);
            case MLG_RUSH:
                return new MlgrushInstallerTask(gamePlugin);
            case GAPPLE:
                return new GAppleInstallerTask(gamePlugin);
            case NO_DEBUFF:
                return new NoDebuffInstallerTask(gamePlugin);
            case BUILD_UHC:
                return new BuildUHCInstallerTask(gamePlugin);
            case CLASSIC:
                return new ClassicInstallerTask(gamePlugin);
            case SPLEEF:
                return new SpleefInstallerTask(gamePlugin);
            case BATTLE_RUSH:
                return new BattleRushInstallerTask(gamePlugin);
            default:
                Bukkit.getLogger().warning(String.format("Unknown type of Practice - %s", PlazmixPracticePlugin.getPracticeMode().getTitle()));
                return null;
        }
    }
}
