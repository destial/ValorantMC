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
    float getSpawnRadius();
    boolean isInUse();
    void pullDownWalls();
    void pullUpWalls();

    void setUse(boolean use);

    default Site getASite() {
        return getSites().stream().filter(s -> s.getSiteType().equals(Site.Type.A)).findFirst().orElse(null);
    }
    default Site getBSite() {
        return getSites().stream().filter(s -> s.getSiteType().equals(Site.Type.B)).findFirst().orElse(null);
    }
    default Site getCSite() {
        return getSites().stream().filter(s -> s.getSiteType().equals(Site.Type.C)).findFirst().orElse(null);
    }
}
