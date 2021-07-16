package xyz.destiall.mc.valorant.utils;

import xyz.destiall.mc.valorant.Valorant;

public class Debugger {
    public static void debug(String message) {
        Valorant.getInstance().getPlugin().getLogger().info(message);
    }
    public static void warn(String message) {
        Valorant.getInstance().getPlugin().getLogger().warning(message);
    }
}
