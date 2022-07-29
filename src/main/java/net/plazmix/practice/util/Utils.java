package net.plazmix.practice.util;

public class Utils {

    public static int getLevelByXP(int xp) {

        int counts = 1;
        while (counts*1500 <= xp) {
            xp -= counts*1500;
            counts++;
        }
        return counts;
    }
}
