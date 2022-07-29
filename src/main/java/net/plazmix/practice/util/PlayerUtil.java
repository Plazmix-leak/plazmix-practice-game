package net.plazmix.practice.util;

import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerUtil {

    private static HashMap<Player, Integer> cooldown = new HashMap<>();

    public static void addPoints(GameUser user , int silver , boolean win) {
        int gold = user.getCache().get(GeneralGameConstants.PRACTICE_TOTAL_POINTS);
        user.getBukkitHandle().sendMessage(GeneralGameConstants.PREFIX + "§7+" + silver + " очков §6Practice§7: (" + (win ? "победа" : "проигрыш")  +")");
        user.getCache().set(GeneralGameConstants.PRACTICE_TOTAL_POINTS, gold + silver);
    }

    public static void addXP(GameUser user , int xpCount , boolean win) {
        int xp = user.getCache().get(GeneralGameConstants.PRACTICE_TOTAL_XP);
        user.getBukkitHandle().sendMessage(GeneralGameConstants.PREFIX + "§d+" + xpCount + " опыта (" + (win ? "победа" : "проигрыш")  + ")");
        if(Utils.getLevelByXP(xp) < Utils.getLevelByXP(xp + xpCount)) {
            user.getBukkitHandle().sendMessage(GeneralGameConstants.PREFIX + "§dВаш уровень §5Practice §d был успешно повышен!");
        }
        user.getCache().set(GeneralGameConstants.PRACTICE_TOTAL_XP , xp + xpCount);
    }

    public static String getBorder(int yaw) {
        switch (yaw) {
            case 0: return "x";
            case 2: return "x";
            case -180: return "x";
            case 178: return "x";
            case 90: return "y";
            case -90: return "y";
            default: throw new IllegalArgumentException();
        }
    }

    public static void startDelayWatcher(){
        GameSchedulers.runTimer(20L, 20L, () -> {
            if(cooldown.size() != 0) {
                cooldown.forEach((p, i) -> {
                    cooldown.put(p, i - 1);
                    if(i - 1 == 0)
                        cooldown.remove(p);
                });
            }
        });
    }

    public static void addCooldownItem(Player player, Integer delay) {
        cooldown.put(player, delay);
    }

    public static boolean hasCooldownItem(Player player) {
        return cooldown.containsKey(player);
    }
}
