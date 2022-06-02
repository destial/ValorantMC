package xyz.destiall.mc.valorant.api.match;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Skull;
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
import xyz.destiall.mc.valorant.api.map.Site;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.listeners.MatchListener;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.Formatter;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Spike implements Module, Listener {
    private final Match match;
    private final ItemStack item;
    private Location plantedLocation;
    private Item drop;
    private ScheduledTask beep;
    private float diffuse;
    private float plant;
    private ScheduledTask place;

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

    public void place(VPlayer player, Location location) {
        player.setPlanting(false);
        player.holdSpike(null);
        plantedLocation = location;
        location.getBlock().setType(item.getType());
        Skull skull = (Skull) location.getBlock().getState();
        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            Field propertiesField = profile.getClass().getDeclaredField("properties");
            propertiesField.setAccessible(true);
            PropertyMap propertyMap = (PropertyMap) propertiesField.get(profile);
            propertyMap.put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYzMThhMTMxNzJhOTllYmZhYjg2NTVlNmM5MjFjNzc0MmQ0ZDdmODcwNTg1ZjQ4OTllYWE0ZjM2NTE5NSJ9fX0="));
            profileField.set(skull, profile);
            skull.update(true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skull.setRawData((byte) 1);
        match.callEvent(new SpikePlaceEvent(this));
        Countdown c = new Countdown(Countdown.Context.AFTER_SPIKE);
        Collection<VPlayer> list = match.getPlayers().values();
        list.forEach(p -> {
            c.getBossBar().addPlayer(p.getPlayer());
            p.sendMessage(Formatter.color("&bThe spike has been planted!"));
        });
        beep = Scheduler.repeatAsync(() -> list.forEach(p -> p.getPlayer().playSound(plantedLocation, Sound.BLOCK_LEVER_CLICK, 5f, 1f)), 20L);
        place = Scheduler.repeatAsync(() -> Effects.spikeRing(plantedLocation.getBlock().getLocation().add(0.5, 0, 0.5), 2), 5L);
        match.setCountdown(c);
        c.onComplete(this::detonate);
        c.start();
    }

    public void setDiffuse(float d) {
        diffuse = d;
    }

    public void setPlant(float p) {
        plant = p;
    }

    public float getPlant() {
        return plant;
    }

    public float getDiffuse() {
        return diffuse;
    }

    public void defuse() {
        match.callEvent(new SpikeDefuseEvent(this));
        match.setCountdown(null);
        match.getDefender().addScore();
        match.getRound().setWinningSide(Team.Side.DEFENDER);
        match.endRound();
        beep.cancel();
        place.cancel();
    }

    public void detonate() {
        match.callEvent(new SpikeDetonateEvent(this));
        match.getAttacker().addScore();
        match.getRound().setWinningSide(Team.Side.ATTACKER);
        match.endRound();
        beep.cancel();
        place.cancel();
        Effects.detonate(plantedLocation);
        AtomicInteger i = new AtomicInteger(1);
        ScheduledTask task = Scheduler.repeat(() -> {
            int r = i.get();
            if (r < 30) i.incrementAndGet();
            Effects.bombSphere(plantedLocation, r);
            Iterator<VPlayer> it = match.getPlayers().values().stream().filter(p -> !p.isDead()).iterator();
            while (it.hasNext()) {
                VPlayer player = it.next();
                if (player.getLocation().distanceSquared(plantedLocation) <= r * r) {
                    if (player.isDead()) continue;
                    MatchListener.handleDeath(player.getPlayer());
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
                VPlayer p = match.getPlayer(uuid);
                if (p == null || p.getTeam().getSide().equals(Team.Side.DEFENDER)) return;
                p.holdSpike(this);
                setDrop(null);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpikeDrop(PlayerDropItemEvent e) {
        VPlayer p = match.getPlayer(e.getPlayer().getUniqueId());
        if (p == null) return;
        if (p.isHoldingSpike() && e.getItemDrop().getItemStack().isSimilar(item)) {
            MatchListener.spikeDropped(p, e.getItemDrop());
        }
    }

    @EventHandler
    public void onSpikeDiffusePlant(PlayerToggleSneakEvent e) {
        VPlayer player = match.getPlayer(e.getPlayer().getUniqueId());
        if (player == null) return;
        if (!isPlanted() && player.getTeam().getSide() == Team.Side.ATTACKER) {
            ItemStack holding = player.getInventory().getItemInMainHand();
            if (match.getMap().getSite(e.getPlayer().getLocation()) != null && holding.isSimilar(item) && e.isSneaking() && !player.isPlanting()) {
                player.setPlanting(true);
            } else if (!e.isSneaking() && player.isPlanting()) {
                plant = 0;
                player.setPlanting(false);
            }
            return;
        }
        if (isPlanted()) {
            if (player.getTeam().getSide() == Team.Side.ATTACKER) return;
            if (plantedLocation.distanceSquared(e.getPlayer().getLocation()) > 5) return;
            if (e.isSneaking() && !player.isDiffusing()) player.setDiffusing(true);
            else if (!e.isSneaking() && player.isDiffusing()) player.setDiffusing(false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;
        VPlayer player = match.getPlayer(e.getPlayer().getUniqueId());
        if (player == null) return;
        if (player.isDiffusing() || player.isPlanting()) {
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ() || e.getTo().getY() > e.getFrom().getY()) {
                e.setCancelled(true);
            }
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
