package xyz.destiall.mc.valorant.classes;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapImpl implements Map {
    private final World world;
    private final float spawnRadius;
    private final Location attacker;
    private final Location defender;
    private final BoundingBox bounds;
    private boolean inUse;
    private final List<Site> sites = new ArrayList<>();
    public MapImpl(World world, BoundingBox bounds, float spawnRadius, Location attacker, Location defender, List<Site> sites) {
        this.world = world;
        this.bounds = bounds;
        this.spawnRadius = spawnRadius;
        this.attacker = attacker;
        this.defender = defender;
        this.sites.addAll(sites);
        inUse = false;
    }
    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public BoundingBox getBounds() {
        return bounds;
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

    @Override
    public boolean isInUse() {
        return inUse;
    }

    @Override
    public void setUse(boolean use) {
        this.inUse = use;
    }
}
