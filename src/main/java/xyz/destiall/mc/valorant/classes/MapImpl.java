package xyz.destiall.mc.valorant.classes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.map.Site;

import java.util.Set;

public class MapImpl implements Map {
    private final Set<Site> sites;
    private final Set<BoundingBox> walls;
    private final World world;
    private final String name;
    private final float spawnRadius;
    private final Location attacker;
    private final Location defender;
    private final BoundingBox bounds;
    private boolean inUse;

    public MapImpl(String name, World world, BoundingBox bounds, float spawnRadius, Location attacker, Location defender, Set<Site> sites, Set<BoundingBox> walls) {
        this.name = name;
        this.world = world;
        this.bounds = bounds;
        this.spawnRadius = spawnRadius;
        this.attacker = attacker;
        this.defender = defender;
        this.sites = sites;
        this.walls = walls;
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
    public String getName() {
        return name;
    }

    @Override
    public Set<Site> getSites() {
        return sites;
    }

    @Override
    public Set<BoundingBox> getWalls() {
        return walls;
    }

    @Override
    public float getSpawnRadius() {
        return spawnRadius;
    }

    @Override
    public Location getAttackerSpawn() {
        for (double theta = -Math.PI; theta <= Math.PI; theta += Math.PI / 10) {
            double x = spawnRadius * Math.cos(theta);
            double z = spawnRadius * Math.sin(theta);
            Location loc = getAttackerCenter();
            loc.add(x, 0, z);
            if (loc.getBlock().isEmpty() && world.getNearbyEntities(loc, 0.1, 0.1, 0.1).stream().noneMatch(e -> e instanceof Player)) {
                return loc;
            }
        }
        return getAttackerCenter();
    }

    @Override
    public Location getDefenderSpawn() {
        for (double theta = -Math.PI; theta <= Math.PI; theta += Math.PI / 10) {
            double x = spawnRadius * Math.cos(theta);
            double z = spawnRadius * Math.sin(theta);
            Location loc = getDefenderCenter();
            loc.add(x, 0, z);
            if (loc.getBlock().isEmpty() && world.getNearbyEntities(loc, 0.1, 0.1, 0.1).stream().noneMatch(e -> e instanceof Player)) {
                return loc;
            }
        }
        return getDefenderCenter();
    }

    @Override
    public Location getAttackerCenter() {
        return attacker.clone();
    }

    @Override
    public Location getDefenderCenter() {
        return defender.clone();
    }

    @Override
    public boolean isInUse() {
        return inUse;
    }

    @Override
    public void pullDownWalls() {
        for (BoundingBox bounds : walls) {
            for (double x = bounds.getMinX(); x <= bounds.getMaxX(); ++x) {
                for (double y = bounds.getMinY(); y <= bounds.getMaxY(); ++y) {
                    for (double z = bounds.getMinZ(); z <=bounds.getMaxZ(); ++z) {
                        Block block = world.getBlockAt((int)x, (int)y, (int)z);
                        if (block.getType().equals(Material.BLUE_STAINED_GLASS)) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void pullUpWalls() {
        for (BoundingBox bounds : walls) {
            for (double x = bounds.getMinX(); x <= bounds.getMaxX(); ++x) {
                for (double y = bounds.getMinY(); y <= bounds.getMaxY(); ++y) {
                    for (double z = bounds.getMinZ(); z <=bounds.getMaxZ(); ++z) {
                        Block block = world.getBlockAt((int)x, (int)y, (int)z);
                        if (block.getType().equals(Material.AIR)) {
                            block.setType(Material.BLUE_STAINED_GLASS);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setUse(boolean use) {
        this.inUse = use;
    }
}
