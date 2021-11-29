package xyz.destiall.mc.valorant.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.PlayerInventory;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Ultimate;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class InventoryListener implements Listener {

    @EventHandler
    public void onHotbarChange(PlayerItemHeldEvent e) {
        VPlayer vPlayer = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (vPlayer == null) return;
        if (vPlayer.isUsingUlt()) {
            e.setCancelled(true);
            return;
        }
        if (vPlayer.isAwaitingUlt()) {
            vPlayer.getUlt().remove();
        }
        Ability ability = vPlayer.getAbilities().keySet().stream().filter(a -> a.getSlot() == e.getNewSlot()).findFirst().orElse(null);
        if (ability == null) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType().isAir()) {
                e.setCancelled(true);
            } else {
                if (e.getNewSlot() == 1) {
                    vPlayer.showHotbar(vPlayer.getPrimaryGun().getName().name());
                    vPlayer.getPlayer().setWalkSpeed(0.8f);
                } else if (e.getNewSlot() == 2) {
                    vPlayer.showHotbar(vPlayer.getSecondaryGun().getName().name());
                    vPlayer.getPlayer().setWalkSpeed(0.5f);
                } else if (e.getNewSlot() == 3) {
                    vPlayer.showHotbar("KNIFE");
                    vPlayer.getPlayer().setWalkSpeed(1);
                } else if (e.getNewSlot() == 4 && vPlayer.isHoldingSpike()) {
                    vPlayer.showHotbar("SPIKE");
                    vPlayer.getPlayer().setWalkSpeed(1);
                }
            }
            return;
        }
        vPlayer.showHotbar(ability.getName());
        if (ability instanceof Ultimate) {
            vPlayer.setAwaitUlt(true);
            ability.use(vPlayer.getPlayer(), vPlayer.getDirection());
            return;
        }
        if (!ability.canHold()) return;
        e.setCancelled(true);
        ability.use(vPlayer.getPlayer(), vPlayer.getDirection());
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
        if (!e.getPlayer().getInventory().equals(e.getInventory())) return;
        if (!(e.getPlayer() instanceof Player)) return;
        Player p = (Player) e.getPlayer();
        VPlayer vPlayer = MatchManager.getInstance().getParticipant(p);
        if (vPlayer == null) return;
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
