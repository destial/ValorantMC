package xyz.destiall.mc.valorant.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.utils.Debugger;

public class TestListener implements Listener {

    @EventHandler
    public void onPlayerLeftClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.LEFT_CLICK_AIR)) {
            Debugger.debug("left click");
            if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SPYGLASS)) {
                Debugger.debug("left click sniper");
            }
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            Debugger.debug("right click");
            if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SPYGLASS)) {
                Debugger.debug("right click sniper");
            }
        }
    }
}
