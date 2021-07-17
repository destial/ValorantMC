package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.Location;

import java.time.Duration;

public interface Smoke {
    void appear(Location location);
    void dissipate();
    Duration getSmokeDuration();
    double getSmokeRange();
}
