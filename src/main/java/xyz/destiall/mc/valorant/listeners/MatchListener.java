package xyz.destiall.mc.valorant.listeners;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import xyz.destiall.mc.valorant.api.events.DeathEvent;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Knife;
import xyz.destiall.mc.valorant.api.player.Participant;
import xyz.destiall.mc.valorant.factories.ItemFactory;
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
        Gun gun = null;
        Knife knife = null;
        if (victim != killer) {
            killer.addKill();
            gun = ItemFactory.getGun(killer.getPlayer().getInventory().getItemInMainHand());
            knife = killer.getKnife().getItem().isSimilar(killer.getPlayer().getInventory().getItemInMainHand()) ? killer.getKnife() : null;
        }
        victim.getMatch().callEvent(new DeathEvent(victim, killer, gun, knife));
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

    @EventHandler
    public void onMatchDeath(DeathEvent e) {
        String symbol = "✇";
        if (e.getGun() != null) {
            switch (e.getGun().getType()) {
                case SNIPER:
                    symbol = "︻╦̵̵̿╤──";
                    break;
                case SHOTGUN:
                    symbol = "︻┳══";
                    break;
                case RIFLE:
                    symbol = "︻╦╤─";
                    break;
                case SMG:
                    symbol = "⌐╦╦═";
                    break;
                case PISTOL:
                    symbol = "╦═";
                    break;
            }
        } else if (e.getKnife() != null) {
            symbol = "\uD83D\uDDE1️";
        }
        String message = e.getKiller().getPlayer().getName() + " " + symbol + " " + e.getVictim().getPlayer().getName();
        for (Participant p : e.getMatch().getPlayers().values()) {
            ChatColor color = ChatColor.RED;
            if (p.getTeam() == e.getKiller().getTeam()) {
                color = ChatColor.BLUE;
            }
            p.getPlayer().sendMessage(color + message);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        Participant participant = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (participant == null) return;
        // TODO: Implement dropping weapons
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickUpItem(EntityPickupItemEvent e) {
        Gun gun = ItemFactory.getGun(e.getItem().getItemStack());
        if (gun == null) return;
        if (!(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
            return;
        }
        Player player = (Player) e.getEntity();
        Participant participant = MatchManager.getInstance().getParticipant(player);
        if (participant == null) {
            e.setCancelled(true);
            return;
        }
    }
}
