package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.events.spike.SpikeDefuseEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikeDetonateEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikePlaceEvent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.listeners.MatchListener;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Spike implements Module, Listener {
    private final Match match;
    private final ItemStack item;
    private Location plantedLocation;
    private Item drop;
    private ScheduledTask beep;
    private float diffuse;

    public Spike(Match match) {
        this.match = match;
        plantedLocation = null;
        item = new ItemStack(Material.PLAYER_HEAD, 1);
        Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:{Id:[I;8954524,1633633952,-1206398228,-402049971],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYzMThhMTMxNzJhOTllYmZhYjg2NTVlNmM5MjFjNzc0MmQ0ZDdmODcwNTg1ZjQ4OTllYWE0ZjM2NTE5NSJ9fX0=\"}]}}}");
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Spike");
        item.setItemMeta(meta);
        Bukkit.getPluginManager().registerEvents(this, Valorant.getInstance().getPlugin());
    }

    public ItemStack getItem() {
        return item;
    }

    public void place(Location location) {
        plantedLocation = location;
        match.callEvent(new SpikePlaceEvent(this));
        Countdown c = new Countdown(Countdown.Context.AFTER_SPIKE);
        for (VPlayer p : match.getPlayers().values()) {
            c.getBossBar().addPlayer(p.getPlayer());
            p.sendMessage("The spike has been planted!");
        }
        beep = Scheduler.repeatAsync(() ->
            match.getPlayers().values().forEach(p ->
                p.getPlayer().playSound(plantedLocation, Sound.BLOCK_LEVER_CLICK, 1f, 1f)), 20L);
        match.setCountdown(c);
        c.onComplete(this::detonate);
        c.start();
    }

    public void setDiffuse(float d) {
        diffuse = d;
    }

    public float getDiffuse() {
        return diffuse;
    }

    public void defuse() {
        match.callEvent(new SpikeDefuseEvent(this));
        match.setCountdown(null);
        match.getDefender().addScore();
        match.endRound();
        beep.cancel();
    }

    public void detonate() {
        match.callEvent(new SpikeDetonateEvent(this));
        match.getAttacker().addScore();
        match.endRound();
        beep.cancel();
        Effects.detonate(plantedLocation);
        AtomicInteger i = new AtomicInteger(1);
        ScheduledTask task = Scheduler.repeat(() -> {
            int r = i.get();
            if (r < 20) i.incrementAndGet();
            Effects.bombSphere(plantedLocation, r);
            for (VPlayer player : match.getPlayers().values()) {
                if (player.isDead()) continue;
                if (player.getLocation().distanceSquared(plantedLocation) <= r * r) {
                    player.getPlayer().damage(player.getPlayer().getMaxHealth(), player.getPlayer());
                }
            }
        }, 1L);
        Scheduler.delay(task::cancel, 20L * 3) ;
    }

    public boolean isPlanted() {
        return plantedLocation != null;
    }

    public Match getMatch() {
        return match;
    }

    public void setDrop(Item drop) {
        if (getDrop() != null) {
            getDrop().remove();
        }
        this.drop = drop;
    }

    public Item getDrop() {
        return drop;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpikePickup(EntityPickupItemEvent e) {
        if (drop == e.getItem()) {
            e.setCancelled(true);
            if (e.getEntity() instanceof Player) {
                UUID uuid = e.getEntity().getUniqueId();
                VPlayer p = match.getPlayers().get(uuid);
                if (p == null || p.getTeam().getSide().equals(Team.Side.DEFENDER)) return;
                p.holdSpike(this);
                setDrop(null);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpikeDrop(PlayerDropItemEvent e) {
        VPlayer p = match.getPlayers().get(e.getPlayer().getUniqueId());
        if (p == null) return;
        if (p.isHoldingSpike() && e.getItemDrop().getItemStack().isSimilar(item)) {
            MatchListener.spikeDropped(p, e.getItemDrop());
        }
    }

    @EventHandler
    public void onSpikeDiffusing(PlayerToggleSneakEvent e) {
        if (!isPlanted()) return;
        if (plantedLocation.distanceSquared(e.getPlayer().getLocation()) >= 4) return;
        VPlayer player = match.getPlayers().get(e.getPlayer().getUniqueId());
        if (player == null) return;
        if (player.getTeam().getSide().equals(Team.Side.ATTACKER)) return;
        if (e.isSneaking() && !player.isDiffusing()) player.setDiffusing(true);
        else if (!e.isSneaking() && player.isDiffusing()) player.setDiffusing(false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getY() == e.getTo().getY() && e.getFrom().getZ() == e.getTo().getZ()) return;
        VPlayer player = match.getPlayers().get(e.getPlayer().getUniqueId());
        if (player == null) return;
        if (!player.isDiffusing()) return;
        e.setCancelled(true);
        //player.setDiffusing(false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpikePlace(BlockPlaceEvent e) {
        if (isPlanted()) return;
        if (e.getItemInHand().isSimilar(item)) {
            // TODO: Fix spike placement on site
            //Site site = match.getMap().getSite(e.getBlock().getLocation());
            //if (site == null) {
            //    e.setCancelled(true);
            //    return;
            //}
            place(e.getBlock().getLocation());
        }
    }

    @Override
    public void destroy() {
        if (plantedLocation != null) {
            plantedLocation.getBlock().setType(Material.AIR);
        }
        setDrop(null);
        HandlerList.unregisterAll(this);
    }
}
