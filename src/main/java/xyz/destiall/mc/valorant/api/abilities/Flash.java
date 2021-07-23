package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface Flash {
    void flash();
    double getFlashDuration();
    double getFlashRange();
    static boolean isSeen(Player player, Entity flash, int range) {
        if (player.getLocation().getWorld() != flash.getWorld()) return false;
        if (player.getLocation().distanceSquared(flash.getLocation()) > range * range) return false;
        if (player.hasLineOfSight(flash)) {
            Vector flashDirection = flash.getLocation().subtract(player.getLocation()).toVector().clone().normalize();
            Vector playerDirection = player.getLocation().getDirection().clone();
            double angle = Math.acos(flashDirection.dot(playerDirection) / flashDirection.length() * playerDirection.length());
            return angle < (Math.PI * 0.5);
        }
        return false;
    }
}
