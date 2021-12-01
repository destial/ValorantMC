package xyz.destiall.mc.valorant.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Ultimate;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class InventoryListener implements Listener {

    @EventHandler
    public void onHotbarChange(PlayerItemHeldEvent e) {
        VPlayer player = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (player == null) return;
        if (player.isUsingUlt()) {
            e.setCancelled(true);
            return;
        }
        if (player.isAwaitingUlt()) {
            player.getUlt().remove();
        }
        Ability ability = player.getAbilities().keySet().stream().filter(a -> a.getSlot() == e.getNewSlot()).findFirst().orElse(null);
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
                player.getPlayer().setWalkSpeed(0.08f);
            } else if (e.getNewSlot() == 1 && player.getSecondaryGun() != null) {
                player.showHotbar(player.getSecondaryGun().getName().name());
                player.getPlayer().setWalkSpeed(0.05f);
            }
            return;
        }
        player.showHotbar(ability.getName());
        if (ability instanceof Ultimate) {
            player.setAwaitUlt(true);
            ability.use();
            return;
        }
        if (!ability.canHold()) return;
        e.setCancelled(true);
        ability.use();
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        MatchManager matchManager = MatchManager.getInstance();
        VPlayer vPlayer = matchManager.getParticipant(e.getPlayer());
        if (vPlayer == null) return;
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
    public void onInventoryMove(InventoryInteractEvent e) {
        if (!(e.getInventory() instanceof PlayerInventory)) return;
        if (!(e.getInventory().getHolder() instanceof Player)) return;
        Player p = (Player) e.getInventory().getHolder();
        VPlayer vPlayer = MatchManager.getInstance().getParticipant(p);
        if (vPlayer == null) return;
        e.setCancelled(true);
    }
}
