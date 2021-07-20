package xyz.destiall.mc.valorant.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.Ultimate;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class InventoryListener implements Listener {

    @EventHandler
    public void onHotbarChange(PlayerItemHeldEvent e) {
        MatchManager matchManager = MatchManager.getInstance();
        Participant participant = matchManager.getParticipant(e.getPlayer());
        if (participant == null) return;
        if (participant.isUsingUlt()) {
            e.setCancelled(true);
            return;
        }
        if (participant.isAwaitingUlt()) {
            participant.getUlt().remove();
        }
        Ability ability = participant.getAbilities().keySet().stream().filter(a -> a.getSlot() == e.getNewSlot()).findFirst().orElse(null);
        if (ability == null) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType().isAir()) {
                e.setCancelled(true);
            } else {
                if (e.getNewSlot() == 1) {
                    participant.showHotbar(participant.getPrimaryGun().getName().name());
                } else if (e.getNewSlot() == 2) {
                    participant.showHotbar(participant.getSecondaryGun().getName().name());
                } else if (e.getNewSlot() == 3) {
                    participant.showHotbar("KNIFE");
                } else if (e.getNewSlot() == 4 && participant.isHoldingSpike()) {
                    participant.showHotbar("SPIKE");
                }
            }
            return;
        }
        participant.showHotbar(ability.getName());
        if (ability instanceof Ultimate) {
            participant.setAwaitUlt(true);
            ability.use(participant.getPlayer(), participant.getDirection());
            return;
        }
        if (!ability.canHold()) return;
        e.setCancelled(true);
        ability.use(participant.getPlayer(), participant.getDirection());
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        MatchManager matchManager = MatchManager.getInstance();
        Participant participant = matchManager.getParticipant(e.getPlayer());
        if (participant == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {
        if (!e.getPlayer().getInventory().equals(e.getInventory())) return;
        if (!(e.getPlayer() instanceof Player)) return;
        Player p = (Player) e.getPlayer();
        Participant participant = MatchManager.getInstance().getParticipant(p);
        if (participant == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMove(InventoryInteractEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        Participant participant = MatchManager.getInstance().getParticipant(p);
        if (participant == null) return;
        e.setCancelled(true);
    }
}
