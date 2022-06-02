package xyz.destiall.mc.valorant.api.abilities;

import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.utils.Versioning;

public interface Flash {
    void flash();
    double getFlashDuration();
    double getFlashRange();
    static boolean isSeen(Player player, Entity flash, int range) {
        if (distanceSquared(player.getLocation(), flash.getX(), flash.getY(), flash.getZ()) > range * range) return false;
        if (Versioning.getPlayer(player).hasLineOfSight(flash)) {
            Vector vect = new Vector(flash.getX(), flash.getY(), flash.getZ());
            Vector flashDirection = vect.subtract(player.getLocation().toVector()).clone().normalize();
            Vector playerDirection = player.getLocation().getDirection().clone();
            double angle = Math.acos(flashDirection.dot(playerDirection) / flashDirection.length() * playerDirection.length());
            return angle < (Math.PI * 0.5);
        }
        return false;
    }

    private static double distanceSquared(Location l, double x, double y, double z) {
        return  NumberConversions.square(l.getX() - x) + NumberConversions.square(l.getY() - y) + NumberConversions.square(l.getZ() - z);
    }
}
