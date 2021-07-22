package xyz.destiall.mc.valorant.classes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.map.Site;

import java.util.HashSet;
import java.util.Set;

public class MapImpl implements Map {
    private final World world;
    private final String name;
    private final float spawnRadius;
    private final Location attacker;
    private final Location defender;
    private final BoundingBox bounds;
    private boolean inUse;
    private final Set<Site> sites = new HashSet<>();
    private final Set<BoundingBox> walls = new HashSet<>();
    public MapImpl(String name, World world, BoundingBox bounds, float spawnRadius, Location attacker, Location defender, Set<Site> sites, Set<BoundingBox> walls) {
        this.name = name;
        this.world = world;
        this.bounds = bounds;
        this.spawnRadius = spawnRadius;
        this.attacker = attacker;
        attacker.setWorld(world);
        this.defender = defender;
        defender.setWorld(world);
        this.sites.addAll(sites);
        this.walls.addAll(walls);
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
        for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 10) {
            double x = spawnRadius * Math.cos(theta);
            double z = spawnRadius * Math.sin(theta);
            Location loc = attacker.clone();
            loc.add(x, 0, z);
            if (loc.getBlock().isEmpty() && world.getNearbyEntities(loc, 0.1, 0.1, 0.1).stream().noneMatch(e -> e instanceof Player)) {
                return loc;
            }
        }
        return attacker;
    }

    @Override
    public Location getDefenderSpawn() {
        for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 10) {
            double x = spawnRadius * Math.cos(theta);
            double z = spawnRadius * Math.sin(theta);
            Location loc = defender.clone();
            loc.add(x, 0, z);
            if (loc.getBlock().isEmpty() && world.getNearbyEntities(loc, 0.1, 0.1, 0.1).stream().noneMatch(e -> e instanceof Player)) {
                return loc;
            }
        }
        return defender;
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
                    for (double z = bounds.getMinZ(); z <=bounds.getMaxX(); ++z) {
                        Location loc = new Location(world, x, y, z);
                        if (loc.getBlock().getType().equals(Material.BARRIER)) {
                            loc.getBlock().setType(Material.AIR);
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
                    for (double z = bounds.getMinZ(); z <=bounds.getMaxX(); ++z) {
                        Location loc = new Location(world, x, y, z);
                        if (loc.getBlock().getType().equals(Material.AIR)) {
                            loc.getBlock().setType(Material.BARRIER);
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
