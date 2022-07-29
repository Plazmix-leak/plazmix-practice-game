package net.plazmix.practice.util;

import net.plazmix.game.GamePlugin;
import net.plazmix.utility.location.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public final class GeneralGameConstants {

    public static final String PREFIX                          = "§d§lPractice §8:: §f";

    /* PLAYER DATABASE CONSTANTS */
    public static final String PRACTICE_TOTAL_XP               = "xp";
    public static final String PRACTICE_TOTAL_POINTS           = "points";

    public static Location LOBBY_LOCATION = null;

    public static void initLocationValues(){
        FileConfiguration config = GamePlugin.getInstance().getConfig();

        LOBBY_LOCATION = LocationUtil.stringToLocation(config.getString("wait-lobby-spawn"));
    }
}
