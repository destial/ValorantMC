package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.items.Drop;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.Map;
import java.util.Objects;

public class DropHandler implements Module, Listener {
    private final Match match;

    public DropHandler(Match match) {
        this.match = match;
    }

    @EventHandler
    public void onGunPickup(EntityPickupItemEvent e) {
        Drop drop = match.getDroppedItems().get(e.getItem());
        if (drop == null || drop.getGun() == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        VPlayer player = match.getPlayer(e.getPlayer().getUniqueId());
        if (player == null) return;
        ItemStack drop = e.getItemDrop().getItemStack();
        if (player.getPrimaryGun() != null && drop.isSimilar(player.getPrimaryGun().getItem())) {
            Drop d = new Drop(match, e.getItemDrop(), e.getItemDrop().getItemStack());
            d.setGun(player.getPrimaryGun());
            player.getMatch().getDroppedItems().put(e.getItemDrop(), d);
            player.setPrimaryGun(null);
            return;
        }
        if (player.getSecondaryGun() != null && drop.isSimilar(player.getSecondaryGun().getItem())) {
            Drop d = new Drop(match, e.getItemDrop(), e.getItemDrop().getItemStack());
            d.setGun(player.getSecondaryGun());
            player.getMatch().getDroppedItems().put(e.getItemDrop(), d);
            player.setSecondaryGun(null);
            return;
        }
        if (player.isHoldingSpike()) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        VPlayer player = match.getPlayer(e.getPlayer().getUniqueId());
        if (player == null) return;

        Location eye = player.getEyeLocation();
        RayTraceResult result = eye.getWorld().rayTraceEntities(eye, eye.getDirection(), 3, i -> i instanceof Item);

        Gun gunAfter = null;
        Item drop = null;
        if (result != null && result.getHitEntity() != null) {
            if (result.getHitEntity() instanceof Item) {
                drop = (Item) result.getHitEntity();
                Drop d = match.getDroppedItems().get(drop);
                if (d != null) {
                    gunAfter = d.getGun();
                }
            }
        }
        if (gunAfter == null) return;

        Gun gunBefore = null;
        ItemStack swapGun = e.getMainHandItem();
        if (swapGun != null) {
            if (player.getPrimaryGun() != null && swapGun.isSimilar(player.getPrimaryGun().getItem())) {
                gunBefore = player.getPrimaryGun();
            } else if (player.getSecondaryGun() != null && swapGun.isSimilar(player.getSecondaryGun().getItem())) {
                gunBefore = player.getSecondaryGun();
            }
        }

        if (gunBefore != null) {
            if (gunBefore.getName().getSlot() == gunAfter.getName().getSlot()) {
                if (gunAfter.getName().getSlot() == Gun.Slot.FIRST) {
                    player.setPrimaryGun(gunAfter);
                    player.getInventory().setItem(0, drop.getItemStack());
                } else {
                    player.setSecondaryGun(gunAfter);
                    player.getInventory().setItem(1, drop.getItemStack());
                }
                drop.setItemStack(swapGun);
            } else {
                Gun gunInSlot = gunAfter.getName().getSlot() == Gun.Slot.FIRST ? player.getPrimaryGun() : player.getSecondaryGun();
                if (gunInSlot != null) {
                    if (gunAfter.getName().getSlot() == Gun.Slot.FIRST) {
                        player.setPrimaryGun(gunAfter);
                        player.getInventory().setItem(0, drop.getItemStack());
                    } else {
                        player.setSecondaryGun(gunAfter);
                        player.getInventory().setItem(1, drop.getItemStack());
                    }
                    drop.setItemStack(Objects.requireNonNull(player.getInventory().getItem(gunInSlot.getName().getSlot().getInt())));
                }
            }
        } else {
            if (gunAfter.getName().getSlot() == Gun.Slot.FIRST) {
                player.setPrimaryGun(gunAfter);
                player.getInventory().setItem(0, drop.getItemStack());
            } else {
                player.setSecondaryGun(gunAfter);
                player.getInventory().setItem(1, drop.getItemStack());
            }
            drop.remove();
            match.getDroppedItems().get(drop).remove();
            match.getDroppedItems().remove(drop);
        }
    }

    public void removeDrops() {
        for (Map.Entry<Item, Drop> entry : match.getDroppedItems().entrySet()) {
            entry.getKey().remove();
            entry.getValue().remove();
        }
        match.getDroppedItems().clear();
    }

    @Override
    public void destroy() {
        removeDrops();
    }
}
