package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.Location;

import java.time.Duration;

public interface Smoke {
    void appear(Location location);

    static void updateSmoke() {}

    void dissipate();
    Duration getSmokeDuration();
    int getSmokeRange();
    Duration getSmokeLastingDuration();
}
