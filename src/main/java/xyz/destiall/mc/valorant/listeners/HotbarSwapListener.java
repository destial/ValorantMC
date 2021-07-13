package xyz.destiall.mc.valorant.listeners;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class HotbarSwapListener implements Listener {

    @EventHandler
    public void onHotbarChange(PlayerItemHeldEvent e) {
        MatchManager matchManager = MatchManager.getInstance();
        Participant participant = matchManager.getMatchFromPlayer(e.getPlayer());
        if (participant == null) return;
        Agent agent = participant.getAgent();
        Ability ability = agent.getAbilites().stream().filter(a -> a.getSlot() == e.getNewSlot()).findFirst().orElse(null);
        if (ability == null) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType().isAir()) {
                e.setCancelled(true);
            }
            return;
        }
        e.setCancelled(true);
        if (ability.getUses() <= 0) return;
        participant.showActionBar(ability.getName());
        double pitch = ((90 - e.getPlayer().getLocation().getPitch()) * Math.PI) / 180;
        double yaw  = ((e.getPlayer().getLocation().getYaw() + 90 + 180) * Math.PI) / 180;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);
        ability.use(participant, new Vector(x, z, y));
    }

    @EventHandler
    public void onSwapLeftAndRight(PlayerSwapHandItemsEvent e) {
        MatchManager matchManager = MatchManager.getInstance();
        Participant participant = matchManager.getMatchFromPlayer(e.getPlayer());
        if (participant == null) return;
        e.setCancelled(true);
    }
}
