package xyz.destiall.mc.valorant.listeners;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.events.player.DeathEvent;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Knife;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Spike;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Formatter;

public class MatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        VPlayer victim = MatchManager.getInstance().getParticipant(p);
        if (victim == null) return;
        Player k = p.getKiller();
        VPlayer killer = victim;
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
        VPlayer victim = MatchManager.getInstance().getParticipant((Player) e.getVictim());
        if (victim == null) return;
        VPlayer damager = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (damager == null) return;
        if (victim.getTeam() == damager.getTeam()) {
            e.setCancelled(true);
            e.setDamage(0);
            e.getDamager().teleport(e.getDamager().getLocation().clone().add(e.getDamager().getVelocity().clone().normalize()));
        }
    }

    @EventHandler
    public void onMatchDeath(DeathEvent e) {
        if (e.getVictim().isHoldingSpike()) {
            spikeDropped(e.getVictim().getSpike(), e.getVictim());
        }
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
        for (VPlayer p : e.getMatch().getPlayers().values()) {
            ChatColor color = ChatColor.RED;
            if (p.getTeam() == e.getKiller().getTeam()) {
                color = ChatColor.BLUE;
            }
            p.getPlayer().sendMessage(color + message);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        VPlayer vPlayer = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (vPlayer == null) return;
        // TODO: Implement dropping weapons

        ItemStack drop = e.getItemDrop().getItemStack();
        if (vPlayer.getPrimaryGun() != null && drop.isSimilar(vPlayer.getPrimaryGun().getItem())) {
            vPlayer.getMatch().getDroppedGuns().add(vPlayer.getPrimaryGun());
            vPlayer.setPrimaryGun(null);
            return;
        }
        if (vPlayer.getSecondaryGun() != null && drop.isSimilar(vPlayer.getSecondaryGun().getItem())) {
            vPlayer.getMatch().getDroppedGuns().add(vPlayer.getSecondaryGun());
            vPlayer.setSecondaryGun(null);
            return;
        }
        if (vPlayer.isHoldingSpike() && drop.isSimilar(Spike.getItem())) {
            spikeDropped(vPlayer.getSpike(), vPlayer);
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickUpItem(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            for (Match match : MatchManager.getInstance().getAllMatches()) {
                if (!match.getMap().getBounds().contains(e.getEntity().getBoundingBox())) continue;
                e.setCancelled(true);
                break;
            }
            return;
        }
        Player player = (Player) e.getEntity();
        VPlayer vPlayer = MatchManager.getInstance().getParticipant(player);
        Gun gun = ItemFactory.getGun(e.getItem().getItemStack());
        if (gun == null) {
            if (Spike.getItem().isSimilar(e.getItem().getItemStack())) {
                if (!vPlayer.getTeam().getSide().equals(Team.Side.ATTACKER)) {
                    e.setCancelled(true);
                    return;
                }
                vPlayer.holdSpike(vPlayer.getMatch().getSpike());
            }
            return;
        }
        if (vPlayer == null) {
            for (Match match : MatchManager.getInstance().getAllMatches()) {
                if (!match.getMap().getBounds().contains(player.getBoundingBox())) continue;
                e.setCancelled(true);
                break;
            }
        }
        e.setCancelled(true);
    }

    private void spikeDropped(Spike spike, VPlayer holder) {
        holder.holdSpike(null);
        for (VPlayer player : holder.getMatch().getPlayers().values()) {
            if (player == holder) continue;
            player.getPlayer().sendTitle(null, Formatter.color("&eSpike dropped"), 0, 1, 0);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        CreationSession session = CreationSession.getSession(e.getPlayer());
        if (session != null) {
            CreationSession.ACTIVE_SESSIONS.remove(session);
        }
    }
}
