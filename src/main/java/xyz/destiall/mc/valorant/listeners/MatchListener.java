package xyz.destiall.mc.valorant.listeners;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.events.player.DeathEvent;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Knife;
import xyz.destiall.mc.valorant.api.player.DeadBody;
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
        victim.getPlayer().spigot().respawn();
        victim.getPlayer().setGameMode(GameMode.SPECTATOR);
        DeadBody body = new DeadBody(victim);
        body.die();
        victim.getMatch().callEvent(new DeathEvent(victim, killer, gun, knife));
        VPlayer spectateTarget = victim.getTeam().getMembers().stream().filter(t -> t != victim).findFirst().orElse(null);
        if (spectateTarget == null) return;
        victim.getPlayer().setSpectatorTarget(spectateTarget.getPlayer());
    }

    @EventHandler
    public void onEscapeSpectator(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.SPECTATE) return;
        if (e.getPlayer().getSpectatorTarget() == null) return;
        VPlayer p = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (p == null) return;
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    public void onMatchDeath(DeathEvent e) {
        if (e.getVictim().isHoldingSpike()) {
            spikeDropped(e.getVictim());
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
        VPlayer player = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (player == null) return;
        // TODO: Implement dropping weapons

        ItemStack drop = e.getItemDrop().getItemStack();
        if (player.getPrimaryGun() != null && drop.isSimilar(player.getPrimaryGun().getItem())) {
            player.getMatch().getDroppedGuns().add(player.getPrimaryGun());
            player.setPrimaryGun(null);
            return;
        }
        if (player.getSecondaryGun() != null && drop.isSimilar(player.getSecondaryGun().getItem())) {
            player.getMatch().getDroppedGuns().add(player.getSecondaryGun());
            player.setSecondaryGun(null);
            return;
        }
        if (player.isHoldingSpike()) return;
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        VPlayer player = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (player == null) return;
        if (!player.isHoldingSpike()) {
            e.setCancelled(true);
            return;
        }
        if (!e.getItemInHand().isSimilar(player.getSpike().getItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        VPlayer player = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (player == null) return;
        e.setCancelled(true);
    }

    private void spikeDropped(VPlayer holder) {
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
