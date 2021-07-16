package xyz.destiall.mc.valorant.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.List;

public interface Map {
    World getWorld();
    BoundingBox getBounds();
    List<Site> getSites();
    Location getAttackerSpawn();
    Location getDefenderSpawn();
    float getSpawnRadius();
    boolean isInUse();

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
