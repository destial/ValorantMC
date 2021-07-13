package xyz.destiall.mc.valorant.api;

import org.bukkit.Location;
import xyz.destiall.mc.valorant.api.events.spike.SpikeDefuseEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikeDetonateEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikePlaceEvent;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Spike {
    private Duration timer;
    private Long timePlaced;
    private Timer spikeTimer;
    private final Match match;
    private Location plantedLocation;
    public Spike(Match match) {
        timer = null;
        spikeTimer = null;
        timePlaced = null;
        this.match = match;
        plantedLocation = null;
    }

    public void place(Location location) {
        plantedLocation = location;
        timer = Duration.of(45L, SECONDS);
        timePlaced = System.currentTimeMillis();
        spikeTimer = new Timer();
        match.callEvent(new SpikePlaceEvent(this));
        spikeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timer = timer.minusMillis(System.currentTimeMillis() - timePlaced);
                if (timer.isZero() || timer.isNegative()) {
                    detonate();
                }
                timePlaced = System.currentTimeMillis();
            }
        }, 0L, 1L);
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
