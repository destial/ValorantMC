package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface Flash {
    static boolean isSeen(Player player, Entity flash, int range) {
        if (player.getLocation().getWorld() != flash.getWorld()) return false;
        if (player.getLocation().distance(flash.getLocation()) > range) return false;
        if (player.hasLineOfSight(flash)) {
            Vector flashDirection = flash.getLocation().subtract(player.getLocation()).toVector().normalize();
            Vector playerDirection = player.getLocation().getDirection().clone();
            // TODO: Use player's direction to figure out line of sight
            return true;
        }
        return false;
    }
}
