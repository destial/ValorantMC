package xyz.destiall.mc.valorant.utils;

import org.bukkit.ChatColor;

import java.time.Duration;

public class Formatter {
    public static String duration(Duration duration) {
        long millis = duration.toMillis();
        double m = 0, s;
        s = millis / 1000D;
        millis -= ((int) s * 1000L);
        while (s > 60D) {
            m++;
            s -= 60D;
        }
        if (m < 1) {
            String mil = "" + millis;
            if (mil.length() > 3) {
                mil = mil.substring(0, 3);
            }
            return (int) s + "." + mil;
        }
        String ss = "" + ((int) s);
        if (ss.length() < 2) ss = "0" + ss;
        return ((int) m) + ":" + ss;
    }

    public static String durationSeconds(Duration duration) {
        long millis = duration.toMillis();
        long s = millis / 1000;
        return "" + s;
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
