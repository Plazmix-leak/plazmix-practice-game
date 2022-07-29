package net.plazmix.practice.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.practice.battlerush.BattleRushInstallerTask;
import net.plazmix.practice.builduhc.BuildUHCInstallerTask;
import net.plazmix.practice.classic.ClassicInstallerTask;
import net.plazmix.practice.gapple.GAppleInstallerTask;
import net.plazmix.practice.mlgrush.MlgrushInstallerTask;
import net.plazmix.practice.nodebuff.NoDebuffInstallerTask;
import net.plazmix.practice.spleef.SpleefInstallerTask;
import net.plazmix.practice.sumo.SumoInstallerTask;
import org.bukkit.ChatColor;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PracticeMode {

    MLG_RUSH(ChatColor.AQUA, "MLGRush" , MlgrushInstallerTask.class),
    SUMO(ChatColor.RED, "Sumo" , SumoInstallerTask.class),
    NO_DEBUFF(ChatColor.DARK_AQUA, "NoDebuff" , NoDebuffInstallerTask.class),
    BUILD_UHC(ChatColor.YELLOW, "BuildUHC" , BuildUHCInstallerTask.class),
    GAPPLE(ChatColor.GOLD, "GApple" , GAppleInstallerTask.class),
    CLASSIC(ChatColor.WHITE, "Classic" , ClassicInstallerTask.class),
    SPLEEF(ChatColor.WHITE, "Spleef" , SpleefInstallerTask.class),
    BATTLE_RUSH(ChatColor.BLUE, "BattleRush" , BattleRushInstallerTask.class),

    @Deprecated
    BRIDGE(ChatColor.GREEN, "Bridge" , null),
    ;

    private ChatColor color;
    private String title;
    private Class<? extends GameInstallerTask> clazz;

}
