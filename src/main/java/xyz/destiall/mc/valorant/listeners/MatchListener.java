package xyz.destiall.mc.valorant.listeners;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.events.DeathEvent;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class MatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Participant victim = MatchManager.getInstance().getParticipant(p);
        if (victim == null) return;
        Player k = p.getKiller();
        Participant killer = victim;
        if (k != null) {
            killer = victim.getMatch().getPlayers().get(k.getUniqueId());
        }
        if (killer == null) {
            killer = victim;
        }
        e.setDeathMessage(null);
        e.setDroppedExp(0);
        e.setKeepInventory(false);
        e.setKeepLevel(false);
        e.setNewTotalExp(0);
        e.setNewExp(0);
        e.setNewLevel(0);
        e.getDrops().clear();
        victim.addDeath();
        if (victim != killer) {
            killer.addKill();
        }
        victim.getMatch().callEvent(new DeathEvent(victim, killer));
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        Participant victim = MatchManager.getInstance().getParticipant((Player) e.getVictim());
        if (victim == null) return;
        Participant damager = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (damager == null) return;
        if (victim.getTeam() == damager.getTeam()) {
            e.setCancelled(true);
            e.setDamage(0);
            e.getDamager().teleport(e.getDamager().getLocation().clone().add(e.getDamager().getVelocity().clone().normalize()));
        }
    }
}
