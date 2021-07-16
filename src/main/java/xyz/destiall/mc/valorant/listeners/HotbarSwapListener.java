package xyz.destiall.mc.valorant.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class HotbarSwapListener implements Listener {

    @EventHandler
    public void onHotbarChange(PlayerItemHeldEvent e) {
        MatchManager matchManager = MatchManager.getInstance();
        Participant participant = matchManager.getParticipant(e.getPlayer());
        if (participant == null) return;
        Ability ability = participant.getAbilities().get(e.getNewSlot());
        if (ability == null) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType().isAir()) {
                e.setCancelled(true);
            }
            return;
        }
        participant.showActionBar(ability.getName());
        if (!ability.canHold()) return;
        e.setCancelled(true);
        ability.use(participant.getPlayer(), participant.getPlayer().getLocation().getDirection());
    }

    @EventHandler
    public void onSwapLeftAndRight(PlayerSwapHandItemsEvent e) {
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
}
