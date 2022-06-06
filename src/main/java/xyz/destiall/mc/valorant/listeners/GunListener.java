package xyz.destiall.mc.valorant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;
import xyz.destiall.mc.valorant.utils.Shooter;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class GunListener implements Listener {
    private final ConcurrentHashMap<Player, Boolean> list = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Player, Boolean> list2 = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Player, ScheduledTask> tasks = new ConcurrentHashMap<>();

    @EventHandler
    public void onGunInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) return;
            if (item.getType() == Material.SPYGLASS) {
                sniperShot(e);
                return;
            }
            Gun gun = ItemFactory.getGun(item);
            if (gun == null) return;
            gun.shoot(e.getPlayer());
        }
    }

    private void sniperShot(PlayerInteractEvent e) {
        ItemMeta meta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
        if (!meta.hasLore()) return;
        if (!meta.getLore().get(0).toUpperCase().contains(Gun.Type.SNIPER.name())) return;
        String dmgRaw = meta.getLore().get(1).substring(("Damage: " + ChatColor.RED).length() + 2);
        double dmg = Double.parseDouble(dmgRaw);
        if (dmg < 0) {
            dmg = 1000;
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            Boolean isHolding = list.computeIfAbsent(e.getPlayer(), k -> true);
            if (isHolding) {
                list2.put(e.getPlayer(), false);
                double finalDmg = dmg;
                ScheduledTask task = Scheduler.repeat(() -> {
                    ScheduledTask t = tasks.get(e.getPlayer());
                    if (t.getTask().isCancelled()) return;
                    if (e.getPlayer().getItemInUse() == null && e.getPlayer().getInventory().getItemInMainHand().equals(e.getItem())) {
                        list.put(e.getPlayer(), false);
                        if (list2.get(e.getPlayer())) return;
                        list2.put(e.getPlayer(), true);
                        Shooter.shoot(e.getPlayer(), e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(), finalDmg, 0, false);
                        t.cancel();
                    } else if (!e.getPlayer().getInventory().getItemInMainHand().equals(e.getItem())) {
                        list.put(e.getPlayer(), false);
                        if (list2.get(e.getPlayer())) return;
                        list2.put(e.getPlayer(), true);
                        t.cancel();
                    }
                }, 1L);
                tasks.put(e.getPlayer(), task);
            } else {
                list.put(e.getPlayer(), true);
                sniperShot(new PlayerInteractEvent(e.getPlayer(), e.getAction(), e.getItem(), e.getClickedBlock(), e.getBlockFace()));
            }
            return;
        }
        if (e.getPlayer().isSneaking()) {
            Shooter.shoot(e.getPlayer(), e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(), dmg, 1);
            return;
        }
        if (e.getPlayer().isSprinting()) {
            Shooter.shoot(e.getPlayer(), e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(), dmg, 3);
            return;
        }
        Shooter.shoot(e.getPlayer(), e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(), dmg, 2);

    }
}
