package xyz.destiall.mc.valorant.api.map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.Set;

public interface Map {
    World getWorld();
    BoundingBox getBounds();
    String getName();
    Set<Site> getSites();
    Set<BoundingBox> getWalls();
    Location getAttackerSpawn();
    Location getDefenderSpawn();
    Location getAttackerCenter();
    Location getDefenderCenter();
    float getSpawnRadius();
    boolean isInUse();
    void pullDownWalls();
    void pullUpWalls();

    void setUse(boolean use);

    default Site getSite(Site.Type type) {
        return getSites().stream().filter(s -> s.getSiteType().equals(type)).findFirst().orElse(null);
    }

    default Site getSite(Location location) {
        return getSites().stream().filter(s -> s.getBounds().contains(location.getX(), location.getY(), location.getZ())).findFirst().orElse(null);
    }

    default Site getASite() {
        return getSite(Site.Type.A);
    }
    default Site getBSite() {
        return getSite(Site.Type.B);
    }
    default Site getCSite() {
        return getSite(Site.Type.C);
    }
}
