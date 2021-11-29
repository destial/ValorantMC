package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.events.spike.SpikeDefuseEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikeDetonateEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikePlaceEvent;
import xyz.destiall.mc.valorant.utils.Formatter;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Spike {
    private Duration timer;
    private Long timePlaced;
    private ScheduledTask spikeTimer;
    private final Match match;
    private Location plantedLocation;
    private static ItemStack item;
    public static ItemStack getItem() {
        if (item == null) {
            item = new ItemStack(Material.COBBLESTONE_WALL, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Formatter.color("&7Spike"));
            meta.setUnbreakable(true);
            meta.getItemFlags().add(ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        return item;
    }
    public Spike(Match match) {
        timer = null;
        spikeTimer = null;
        timePlaced = null;
        this.match = match;
        plantedLocation = null;
        item = new ItemStack(Material.COBBLESTONE_WALL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Spike");
        item.setItemMeta(meta);
    }

    public void place(Location location) {
        plantedLocation = location;
        timer = Duration.of(45L, SECONDS);
        timePlaced = System.currentTimeMillis();
        match.callEvent(new SpikePlaceEvent(this));
        spikeTimer = Scheduler.repeat(() -> {
            timer = timer.minusMillis(System.currentTimeMillis() - timePlaced);
            if (timer.isZero() || timer.isNegative()) {
                detonate();
                spikeTimer.cancel();
            }
            timePlaced = System.currentTimeMillis();
        }, 1L);
    }

    public void defuse() {
        if (spikeTimer == null) return;
        spikeTimer.cancel();
        match.callEvent(new SpikeDefuseEvent(this));
    }

    public void detonate() {
        if (spikeTimer == null) return;
        spikeTimer.cancel();
        match.callEvent(new SpikeDetonateEvent(this));
    }

    public boolean isPlaced() {
        return plantedLocation != null;
    }

    public Duration getTimer() {
        return timer;
    }

    public Match getMatch() {
        return match;
    }
}
