package xyz.destiall.mc.valorant.listeners;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class CSListener implements Listener {

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        VPlayer victim = MatchManager.getInstance().getPlayer((Player) e.getVictim());
        if (victim == null) return;
        VPlayer damager = MatchManager.getInstance().getPlayer(e.getPlayer());
        if (damager == null) return;
        if (victim.getTeam() == damager.getTeam()) {
            e.setCancelled(true);
        } else {
            victim.setLastDamage(e);
        }
    }
}
