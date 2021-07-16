package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Flash {
    void updateFlash();
    static boolean isSeen(Player player, Entity flash, int range) {
        if (player.getLocation().distance(flash.getLocation()) > range) return false;
        return player.hasLineOfSight(flash);
    }
}
