package xyz.destiall.mc.valorant.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.events.player.DeathEvent;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Knife;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Countdown;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.api.sidebar.SidebarHandler;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Formatter;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MatchListener implements Listener {
    private final Map<UUID, Match> leftPlayers = new HashMap<>();

    public MatchListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Valorant.getInstance().getPlugin(), PacketType.Play.Server.PLAYER_COMBAT_KILL) {
            @Override
            public void onPacketSending(PacketEvent event) {
                VPlayer player = MatchManager.getInstance().getPlayer(event.getPlayer());
                if (player != null) return;
                super.onPacketSending(event);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerVanillaDeath(PlayerDeathEvent e) {
        e.getEntity().spigot().respawn();
        e.setDeathMessage(null);
        e.setKeepLevel(false);
        e.setNewExp(0);
        e.setNewLevel(0);
        e.setNewTotalExp(0);
        e.setKeepInventory(false);
        e.setDroppedExp(0);
        e.getDrops().clear();
        handleDeath(e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (e.getFinalDamage() >= p.getHealth()) {
            e.setCancelled(true);
            handleDeath(p);
        }
    }

    @EventHandler
    public void onSpectator(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.SPECTATE) return;
        if (e.getPlayer().getSpectatorTarget() == null) return;
        VPlayer p = MatchManager.getInstance().getPlayer(e.getPlayer());
        if (p == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        VPlayer victim = MatchManager.getInstance().getPlayer((Player) e.getVictim());
        if (victim == null) return;
        VPlayer damager = MatchManager.getInstance().getPlayer(e.getPlayer());
        if (damager == null) return;
        if (victim.getTeam() == damager.getTeam()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMatchDeath(DeathEvent e) {
        String symbol = "✇";
        if (e.getGun() != null) {
            switch (e.getGun().getType()) {
                case SNIPER -> symbol = "︻╦̵̵̿╤──";
                case SHOTGUN -> symbol = "︻┳══";
                case RIFLE -> symbol = "︻╦╤─";
                case SMG -> symbol = "⌐╦╦═";
                case PISTOL -> symbol = "╦═";
                default -> {
                }
            }
        } else if (e.getKnife() != null) {
            symbol = "\uD83D\uDDE1️";
        }
        String message = e.getKiller().getPlayer().getName() + " " + symbol + " " + e.getVictim().getPlayer().getName();
        Collection<VPlayer> list = e.getMatch().getPlayers().values();
        for (VPlayer p : list) {
            ChatColor color = ChatColor.RED;
            if (p.getTeam() == e.getKiller().getTeam()) {
                color = ChatColor.BLUE;
            }
            p.getPlayer().sendMessage(color + message);
        }
        if (!e.getMatch().getRound().isOver() && e.getVictim().getTeam().getMembers().stream().allMatch(VPlayer::isDead)) {
            if (e.getVictim().getTeam().getSide() == Team.Side.ATTACKER) {
                e.getMatch().getDefender().addScore();
                e.getMatch().endRound();
            } else if (e.getMatch().getModule(Countdown.class).getContext() == Countdown.Context.BEFORE_SPIKE) {
                e.getMatch().getAttacker().addScore();
                e.getMatch().endRound();
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        VPlayer player = MatchManager.getInstance().getPlayer(e.getPlayer());
        if (player == null) return;
        // TODO: Implement dropping weapons
        ItemStack drop = e.getItemDrop().getItemStack();
        if (player.getPrimaryGun() != null && drop.isSimilar(player.getPrimaryGun().getItem())) {
            player.getMatch().getDroppedGuns().put(e.getItemDrop(), player.getPrimaryGun());
            player.setPrimaryGun(null);
            return;
        }
        if (player.getSecondaryGun() != null && drop.isSimilar(player.getSecondaryGun().getItem())) {
            player.getMatch().getDroppedGuns().put(e.getItemDrop(), player.getSecondaryGun());
            player.setSecondaryGun(null);
            return;
        }
        if (player.isHoldingSpike()) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        VPlayer player = MatchManager.getInstance().getPlayer(e.getPlayer());
        if (player == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        VPlayer player = MatchManager.getInstance().getPlayer(e.getPlayer());
        if (player == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        CreationSession session = CreationSession.getSession(e.getPlayer());
        if (session != null) {
            CreationSession.ACTIVE_SESSIONS.remove(session);
        }
        VPlayer player = MatchManager.getInstance().getPlayer(e.getPlayer());
        if (player != null) {
            e.setQuitMessage(null);
            leftPlayers.put(player.getUUID(), player.getMatch());
            if (player.getMatch().isEmpty()) {
                player.getMatch().end(MatchTerminateEvent.Reason.CANCEL);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getWalkSpeed() != 0.2f) e.getPlayer().setWalkSpeed(0.2f);
        if (leftPlayers.containsKey(e.getPlayer().getUniqueId())) {
            Match match = leftPlayers.get(e.getPlayer().getUniqueId());
            if (match.isOver()) {
                e.getPlayer().sendMessage(Formatter.color("&cThe match ended while you were gone..."));
                e.getPlayer().getInventory().clear();
                e.getPlayer().teleport(MatchManager.getInstance().getLobby());
                return;
            }
            e.getPlayer().sendMessage(Formatter.color("&eRejoining the match..."));
            Scheduler.delay(() -> {
                if (!e.getPlayer().isOnline()) return;
                VPlayer player = MatchManager.getInstance().getPlayer(e.getPlayer());
                if (player == null) return;
                player.rejoin(e.getPlayer());
                SidebarHandler sidebar = player.getMatch().getModule(SidebarHandler.class);
                Countdown countdown = player.getMatch().getModule(Countdown.class);
                countdown.getBossBar().addPlayer(e.getPlayer());
                sidebar.rejoin(player);
                leftPlayers.remove(e.getPlayer().getUniqueId());
            }, 20L);
        }
    }

    public static void spikeDropped(VPlayer holder, Item drop) {
        Collection<VPlayer> list = holder.getMatch().getPlayers().values();
        for (VPlayer player : list) {
            player.showSubTitle(Formatter.color("&eSpike dropped"));
        }
        holder.getSpike().setDrop(drop);
        holder.holdSpike(null);
    }

    public static void handleDeath(Player p) {
        VPlayer victim = MatchManager.getInstance().getPlayer(p);
        if (victim == null || victim.isDead()) return;
        Player k = p.getKiller();
        VPlayer killer;
        if (k != null) {
            killer = victim.getMatch().getPlayer(k.getUniqueId());
        } else {
            killer = victim;
        }
        victim.getPlayer().setHealth(victim.getPlayer().getMaxHealth());
        victim.addDeath();
        Gun gun = null;
        Knife knife = null;
        if (victim != killer) {
            killer.addKill();
            gun = ItemFactory.getGun(killer.getPlayer().getInventory().getItemInMainHand());
            knife = killer.getKnife().getItem().isSimilar(killer.getPlayer().getInventory().getItemInMainHand()) ? killer.getKnife() : null;
        }
        victim.setDead(true);
        victim.getPlayer().setGameMode(GameMode.SPECTATOR);

        DeathEvent deathEvent = new DeathEvent(victim, killer, gun, knife);
        for (ItemStack item : victim.getPlayer().getInventory()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (victim.isHoldingSpike() && item.isSimilar(victim.getMatch().getSpike().getItem())) {
                Item drop = victim.getPlayer().getWorld().dropItem(victim.getLocation(), item);
                spikeDropped(victim, drop);
                deathEvent.getDrops().put(drop, item);
                continue;
            }
            if ((victim.getPrimaryGun() != null && item.isSimilar(victim.getPrimaryGun().getItem())) ||
                    (victim.getSecondaryGun() != null && item.isSimilar(victim.getSecondaryGun().getItem()))) {
                Item drop = victim.getPlayer().getWorld().dropItem(victim.getLocation(), item);
                deathEvent.getDrops().put(drop, item);
            }
        }
        victim.getPlayer().getInventory().clear();
        victim.getMatch().callEvent(deathEvent);
        VPlayer spectateTarget = victim.getTeam().getMembers().stream().filter(t -> t != victim).findFirst().orElse(null);
        if (spectateTarget == null) return;
        victim.getPlayer().setSpectatorTarget(spectateTarget.getPlayer());
    }
}
