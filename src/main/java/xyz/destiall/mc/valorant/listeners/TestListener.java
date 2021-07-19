package xyz.destiall.mc.valorant.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.utils.Debugger;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestListener implements Listener {

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        // TODO: Figure out sniper
        if (e.getAction().equals(Action.LEFT_CLICK_AIR)) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SPYGLASS)) {

            }
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SPYGLASS)) {
                Debugger.debug("rightclick");
            }
        }
        test(e);
    }

    private final HashMap<Player, Boolean> list = new HashMap<>();

    public void test(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SPYGLASS)){
            Boolean isHolding = list.computeIfAbsent(e.getPlayer(), k -> true);
            if (isHolding) {
                e.getPlayer().sendMessage("holding");
                Scheduler.delay(() -> {
                    if (e.getPlayer().getItemInUse() != e.getItem()) {
                        list.put(e.getPlayer(), false);
                        e.getPlayer().sendMessage("not holding");
                        return;
                    }
                    //if (e.useItemInHand())
                }, 1L);
            } else {
                list.put(e.getPlayer(), true);
                Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(e.getPlayer(), e.getAction(), e.getItem(), e.getClickedBlock(), e.getBlockFace()));
            }
        }
    }
}
