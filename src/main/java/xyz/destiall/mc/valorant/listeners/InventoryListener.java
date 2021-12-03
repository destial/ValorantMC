package xyz.destiall.mc.valorant.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class InventoryListener implements Listener {

    @EventHandler
    public void onHotbarChange(PlayerItemHeldEvent e) {
        VPlayer player = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (player == null) return;
        Ability ability = player.getAbilities().get(e.getNewSlot());
        if (ability == null) {
            ItemStack newSlot = e.getPlayer().getInventory().getItem(e.getNewSlot());
            if (newSlot == null) {
                e.setCancelled(true);
                return;
            }
            if (newSlot.getType().isAir()) {
                e.setCancelled(true);
                return;
            }
            if (e.getNewSlot() == 0 && player.getPrimaryGun() != null) {
                player.showHotbar(player.getPrimaryGun().getName().name());
            } else if (e.getNewSlot() == 1 && player.getSecondaryGun() != null) {
                player.showHotbar(player.getSecondaryGun().getName().name());
            }
            return;
        }
        player.showHotbar(ability.getName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.hasItem()) return;
        if (e.getAction() != Action.PHYSICAL) {
            VPlayer player = MatchManager.getInstance().getParticipant(e.getPlayer());
            if (player == null) return;
            Ability ability = player.getAbilities().get(e.getPlayer().getInventory().getHeldItemSlot());
            if (ability == null) return;
            Ability.Trigger trigger = ability.getTrigger();
            boolean use = false;
            if (trigger == Ability.Trigger.RIGHT && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) use = true;
            else if (trigger == Ability.Trigger.LEFT && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) use = true;
            if (use) ability.use();
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        MatchManager matchManager = MatchManager.getInstance();
        VPlayer player = matchManager.getParticipant(e.getPlayer());
        if (player == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {
        VPlayer player = MatchManager.getInstance().getParticipant((Player) e.getPlayer());
        if (player == null) return;
        if (player.getMatch().isWaitingForPlayers()) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!(e.getClickedInventory().getHolder() == e.getWhoClicked())) return;
        Player p = (Player) e.getClickedInventory().getHolder();
        VPlayer player = MatchManager.getInstance().getParticipant(p);
        if (player == null) return;
        e.setCancelled(true);
    }
}
