package xyz.destiall.mc.valorant.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.destiall.mc.valorant.Valorant;

public class TestListener implements Listener {

    @EventHandler
    public void onPlayerLeftClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.LEFT_CLICK_AIR)) {
            Valorant.getInstance().getPlugin().getLogger().info("left click");
            if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SPYGLASS)) {
                Valorant.getInstance().getPlugin().getLogger().info("left click sniper");
            }
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            Valorant.getInstance().getPlugin().getLogger().info("right click");
            if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SPYGLASS)) {
                Valorant.getInstance().getPlugin().getLogger().info("right click sniper");
            }
        }
    }
}
