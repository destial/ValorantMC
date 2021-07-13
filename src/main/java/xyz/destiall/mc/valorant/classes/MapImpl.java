package xyz.destiall.mc.valorant.classes;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Site;

import java.util.List;

public class MapImpl implements Map {
    private final World world;
    private final float spawnRadius;
    private final Location attacker;
    private final Location defender;
    private final List<Site> sites;
    public MapImpl(FileConfiguration yaml) {

    }
    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public BoundingBox getBounds() {
        return null;
    }

    @Override
    public List<Site> getSites() {
        return sites;
    }

    @Override
    public float getSpawnRadius() {
        return spawnRadius;
    }

    @Override
    public Location getAttackerSpawn() {
        return attacker;
    }

    @Override
    public Location getDefenderSpawn() {
        return defender;
    }
}
