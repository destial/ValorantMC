package xyz.destiall.mc.valorant.utils;

import java.time.Duration;

public class Formatter {
    public static String duration(Duration duration) {
        long millis = duration.toMillis();
        double m = 0, s = 0;
        s = (double) millis / 1000;
        millis = millis - ((int) s * 1000);
        m = s / 60;
        if (m < 1) {
            String mil = String.valueOf(millis);
            if (mil.length() > 3) {
                mil = mil.substring(0, 3);
            }
            return (int) s + "." + mil;
        }
        return ((int) m) + ":" + ((int) s);
    }
}
