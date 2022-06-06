package xyz.destiall.mc.valorant.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.events.weapon.SniperShotEvent;
import xyz.destiall.mc.valorant.api.topbar.TopbarHandler;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.events.player.DeathEvent;
import xyz.destiall.mc.valorant.api.items.Drop;
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
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.Formatter;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MatchListener extends PacketAdapter implements Listener {
    private final Map<UUID, Match> leftPlayers = new HashMap<>();

    public MatchListener() {
        super(Valorant.plugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT, PacketType.Play.Server.ENTITY_SOUND);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        VPlayer vp = MatchManager.getInstance().getPlayer(player);
        if (vp == null) return;
        PacketContainer packet = event.getPacket();
        Sound sound = packet.getSoundEffects().readSafely(0);
        if (sound.name().contains("BLOCK_") && sound.name().contains("_STEP")) {
            if (!vp.getPlayer().isSprinting()) {
                return;
            }
            packet.getFloat().write(0, packet.getFloat().read(0) * 5);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerVanillaDeath(PlayerDeathEvent e) {
        e.getEntity().spigot().respawn();
        e.setDeathMessage(null);
        e.setKeepLevel(false);
        e.setNewExp(0);
        e.setNewLevel(0);
        e.setNewTotalExp(0);
        e.setKeepInventory(true);
        e.setDroppedExp(0);
        e.getDrops().clear();
        handleDeath(e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;

        if (e instanceof EntityDamageByEntityEvent ev && ev.getDamager() instanceof Player d) {
            VPlayer damager = MatchManager.getInstance().getPlayer(d);
            VPlayer victim = MatchManager.getInstance().getPlayer(p);
            if (damager != null && victim != null && damager.getTeam() == victim.getTeam()) {
                e.setCancelled(true);
                return;
            }
        }

        if (e.getFinalDamage() >= p.getHealth()) {
            if (handleDeath(p)) {
                e.setCancelled(true);
            }
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
        } else if (e.isSniper()) {
            symbol = "︻╦̵̵̿╤──";
        }
        if (e.isHeadshot()) {
            symbol += " ✸";
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
            if (e.getVictim().getTeam().getSide() == Team.Side.DEFENDER) {
                e.getMatch().getAttacker().addScore();
                e.getMatch().getRound().setWinningSide(Team.Side.ATTACKER);
                e.getMatch().endRound();
            } else if (e.getMatch().getModule(Countdown.class).getContext() == Countdown.Context.BEFORE_SPIKE) {
                e.getMatch().getDefender().addScore();
                e.getMatch().getRound().setWinningSide(Team.Side.DEFENDER);
                e.getMatch().endRound();
            }
        }
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

    // @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        VPlayer player = MatchManager.getInstance().getPlayer(e.getPlayer());
        if (player == null) return;
        if (e.getPlayer().isSprinting()) {
            Effects.walking(player);
        }
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
                TopbarHandler topbar = player.getMatch().getModule(TopbarHandler.class);

                topbar.rejoin(player);
                sidebar.rejoin(player);
                leftPlayers.remove(e.getPlayer().getUniqueId());
            }, 20L);
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent e) {
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.CUSTOM) return;
        if (e.getEntity() instanceof Player p) {
            VPlayer player = MatchManager.getInstance().getPlayer(p);
            if (player == null) return;
            e.setCancelled(true);
        }
    }

    public static void spikeDropped(VPlayer holder, Item drop) {
        Collection<VPlayer> list = holder.getMatch().getPlayers().values();
        for (VPlayer player : list) {
            player.sendMessage(Formatter.color("&eSpike dropped"));
        }
        holder.getSpike().setDrop(drop);
        holder.holdSpike(null);
    }

    public static boolean handleDeath(Player p) {
        VPlayer victim = MatchManager.getInstance().getPlayer(p);
        if (victim == null || victim.isDead()) return false;
        Player k = p.getKiller();
        VPlayer killer;
        boolean headshot = false;
        boolean sniper = false;
        if (k != null) {
            killer = victim.getMatch().getPlayer(k.getUniqueId());
        } else {
            if (victim.getLastDamage() != null && victim.getLastDamage() instanceof WeaponDamageEntityEvent e) {
                killer = victim.getMatch().getPlayer(e.getPlayer().getUniqueId());
                headshot = e.isHeadshot();
                victim.setLastDamage(null);
            } else if (victim.getLastDamage() != null && victim.getLastDamage() instanceof SniperShotEvent e) {
                killer = victim.getMatch().getPlayer(e.getDamager().getUniqueId());
                headshot = e.isHeadshot();
                victim.setLastDamage(null);
                sniper = true;
            } else {
                killer = victim;
            }
        }
        victim.getPlayer().setHealth(victim.getPlayer().getMaxHealth());
        victim.addDeath();
        Gun gun = null;
        Knife knife = null;
        if (victim != killer) {
            killer.addKill();
            ItemStack hand = killer.getPlayer().getInventory().getItemInMainHand();
            if (hand.getItemMeta() != null) {
                gun = ItemFactory.getGun(hand);
                knife = killer.getKnife().getItem().isSimilar(hand) ? killer.getKnife() : null;
            }
        }
        victim.setDead(true);
        victim.getPlayer().setGameMode(GameMode.SPECTATOR);

        DeathEvent deathEvent = new DeathEvent(victim, killer, gun, knife, headshot, sniper);
        for (ItemStack item : victim.getPlayer().getInventory()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (victim.isHoldingSpike() && item.isSimilar(victim.getMatch().getSpike().getItem())) {
                Item drop = victim.getPlayer().getWorld().dropItem(victim.getLocation(), item);
                Drop d = new Drop(victim.getMatch(), drop, item);
                d.setSpike(victim.getSpike());
                deathEvent.getDrops().put(drop, d);
                spikeDropped(victim, drop);
                continue;
            }
            if (victim.getPrimaryGun() != null && item.isSimilar(victim.getPrimaryGun().getItem())) {
                Item drop = victim.getPlayer().getWorld().dropItem(victim.getLocation(), item);
                Drop d = new Drop(victim.getMatch(), drop, item);
                d.setGun(victim.getPrimaryGun());
                deathEvent.getDrops().put(drop, d);
            }
            if (victim.getSecondaryGun() != null && item.isSimilar(victim.getSecondaryGun().getItem())) {
                Item drop = victim.getPlayer().getWorld().dropItem(victim.getLocation(), item);
                Drop d = new Drop(victim.getMatch(), drop, item);
                d.setGun(victim.getSecondaryGun());
                deathEvent.getDrops().put(drop, d);
            }
        }
        victim.getPlayer().getInventory().clear();
        victim.getMatch().callEvent(deathEvent);
        for (Map.Entry<Item, Drop> drops : deathEvent.getDrops().entrySet()) {
            victim.getMatch().getDroppedItems().put(drops.getKey(), drops.getValue());
        }
        VPlayer spectateTarget = victim.getTeam().getMembers().stream().filter(t -> t != victim).findFirst().orElse(null);
        if (spectateTarget == null) return true;
        victim.getPlayer().setSpectatorTarget(spectateTarget.getPlayer());
        return true;
    }
}
