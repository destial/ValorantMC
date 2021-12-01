package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.events.spike.SpikeDefuseEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikeDetonateEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikePlaceEvent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Formatter;

import java.util.UUID;

public class Spike implements Module, Listener {
    private final Match match;
    private final ItemStack item;
    private Location plantedLocation;

    public Spike(Match match) {
        this.match = match;
        plantedLocation = null;
        item = new ItemStack(Material.COBBLESTONE_WALL, 1);
        ItemMeta meta = item.getItemMeta();
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
        Countdown c = match.getModule(Countdown.class);
        if (c != null) match.removeModule(c);
        c = new Countdown(Countdown.Context.AFTER_SPIKE);
        for (VPlayer p : match.getPlayers().values()) {
            c.getBossBar().addPlayer(p.getPlayer());
        }
        c.start();
        c.onComplete(() -> {
            detonate();
            match.removeModule(this);
        });
    }

    public void defuse() {
        match.callEvent(new SpikeDefuseEvent(this));
        Countdown c = match.getModule(Countdown.class);
        match.removeModule(c);
    }

    public void detonate() {
        match.callEvent(new SpikeDetonateEvent(this));
    }

    public boolean isPlaced() {
        return plantedLocation != null;
    }

    public Match getMatch() {
        return match;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (e.getItem().getItemStack().isSimilar(item)) {
            if (e.getEntity() instanceof Player) {
                UUID uuid = e.getEntity().getUniqueId();
                VPlayer p = match.getPlayers().get(uuid);
                if (p == null || p.getTeam().getSide().equals(Team.Side.DEFENDER)) {
                    e.setCancelled(true);
                    return;
                }
                p.holdSpike(this);
            }
        }
    }

    @EventHandler
    public void onItemDrop(EntityDropItemEvent e) {
        if (e.getEntity() instanceof Player) {
            VPlayer p = match.getPlayers().get(e.getEntity().getUniqueId());
            if (p == null) return;
            if (p.isHoldingSpike() && e.getItemDrop().getItemStack().isSimilar(item)) {
                p.holdSpike(null);
                for (VPlayer player : match.getPlayers().values()) {
                    if (player == p) continue;
                    player.getPlayer().sendTitle(null, Formatter.color("&eSpike dropped"), 0, 1, 0);
                }
            }
        }
    }

    @EventHandler
    public void onItemPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().isSimilar(item)) {
            place(e.getBlock().getLocation());
        }
    }

    @Override
    public void destroy() {
        HandlerList.unregisterAll(this);
    }
}
