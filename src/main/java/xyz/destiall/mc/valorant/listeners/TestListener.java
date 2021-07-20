package xyz.destiall.mc.valorant.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.HashMap;

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

            }
        }
        test(e);
    }

    private final HashMap<Player, Boolean> list = new HashMap<>();
    private final HashMap<Player, Boolean> list2 = new HashMap<>();
    private final HashMap<Player, ScheduledTask> tasks = new HashMap<>();

    public void test(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SPYGLASS)){
            Boolean isHolding = list.computeIfAbsent(e.getPlayer(), k -> true);
            if (isHolding) {
                list2.put(e.getPlayer(), false);
                e.getPlayer().sendMessage("scoping");
                ScheduledTask task = Scheduler.repeat(() -> {
                    ScheduledTask t = tasks.get(e.getPlayer());
                    if (t.getTask().isCancelled()) return;
                    if (e.getPlayer().getItemInUse() == null || !e.getPlayer().getItemInUse().equals(e.getItem())) {
                        list.put(e.getPlayer(), false);
                        if (list2.get(e.getPlayer())) return;
                        list2.put(e.getPlayer(), true);
                        e.getPlayer().sendMessage("shoot");
                        t.cancel();
                    }
                }, 1L);
                tasks.put(e.getPlayer(), task);
            } else {
                list.put(e.getPlayer(), true);
                Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(e.getPlayer(), e.getAction(), e.getItem(), e.getClickedBlock(), e.getBlockFace()));
            }
        }
    }
}
