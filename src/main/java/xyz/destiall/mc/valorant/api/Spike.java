package xyz.destiall.mc.valorant.api;

import xyz.destiall.mc.valorant.api.events.spike.SpikeDefuseEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikeDetonateEvent;
import xyz.destiall.mc.valorant.api.events.spike.SpikePlaceEvent;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Spike {
    private boolean placed;
    private Duration timer;
    private Long timePlaced;
    private Timer spikeTimer;
    private final Match match;
    public Spike(Match match) {
        placed = false;
        timer = null;
        spikeTimer = null;
        timePlaced = null;
        this.match = match;
    }

    public void place() {
        placed = true;
        timer = Duration.of(45L, SECONDS);
        timePlaced = System.currentTimeMillis();
        spikeTimer = new Timer();
        final Spike spike = this;
        match.callEvent(new SpikePlaceEvent(spike));
        spikeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timer = timer.minusMillis(System.currentTimeMillis() - timePlaced);
                if (timer.isZero() || timer.isNegative()) {
                    this.cancel();
                    match.callEvent(new SpikeDetonateEvent(spike));
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

    public boolean isPlaced() {
        return placed;
    }

    public Duration getTimer() {
        return timer;
    }

    public Match getMatch() {
        return match;
    }
}
