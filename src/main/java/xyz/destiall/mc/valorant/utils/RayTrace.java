package xyz.destiall.mc.valorant.utils;

import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RayTrace {
    private final Vector origin, direction;
    private boolean render;

    public RayTrace(Vector origin, Vector direction) {
        this.origin = origin.clone();
        this.direction = direction.clone().normalize();
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public Vector getDirection() {
        return direction;
    }

    public Vector getOrigin() {
        return origin;
    }

    public Set<Entity> getHitEntities(World world, double blocksAway, double accuracy) {
        Location location = new Location(world, origin.getX(), origin.getY(), origin.getZ());
        Set<Entity> entities = new HashSet<>();
        for (Vector position : traverse(world, blocksAway, accuracy)) {
            location.setX(position.getX());
            location.setY(position.getY());
            location.setZ(position.getZ());
            Block block = location.getBlock();

            if (block.isPassable() || block.isEmpty()) continue;

            if (intersects(position, block.getBoundingBox())) {
                return entities;
            }

            for (Entity entity : world.getNearbyEntities(location, 2, 2, 2)) {
                if (intersects(position, entity.getBoundingBox())) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    public Entity getFirstEntityHit(World world, double blocksAway, double accuracy) {
        Location location = new Location(world, origin.getX(), origin.getY(), origin.getZ());
        RayTraceResult result = world.rayTraceEntities(location, direction, blocksAway);
        return result != null ? result.getHitEntity() : null;
    }

    public Block getFirstHitBlock(World world, double blocksAway, double accuracy) {
        Location location = new Location(world, origin.getX(), origin.getY(), origin.getZ());
        RayTraceResult result = world.rayTraceBlocks(location, direction, blocksAway, FluidCollisionMode.NEVER, true);
        return result != null ? result.getHitBlock() : null;
    }

    public Set<Block> getHitBlocks(World world, double blocksAway, double accuracy) {
        Location location = new Location(world, origin.getX(), origin.getY(), origin.getZ());
        Set<Block> blocks = new HashSet<>();
        for (Vector position : traverse(world, blocksAway, accuracy)) {
            location.setX(position.getX());
            location.setY(position.getY());
            location.setZ(position.getZ());
            Block block = location.getBlock();
            if (intersects(position, block.getBoundingBox())) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public Vector getPostion(double blocksAway) {
        return origin.clone().add(direction.clone().multiply(blocksAway));
    }

    public boolean isOnLine(Vector position) {
        double t = (position.getX() - origin.getX()) / direction.getX();
        return position.getBlockY() == origin.getY() + (t * direction.getY()) && position.getBlockZ() == origin.getZ() + (t * direction.getZ());
    }

    public List<Vector> traverse(World world, double blocksAway, double accuracy) {
        Location location = new Location(world, origin.getX(), origin.getY(), origin.getZ());
        List<Vector> positions = new ArrayList<>();
        for (double d = 0; d <= blocksAway; d += accuracy) {
            double x = direction.getX() * d;
            double y = direction.getY() * d;
            double z = direction.getZ() * d;
            origin.setX(origin.getX() + x);
            origin.setY(origin.getY() + y);
            origin.setZ(origin.getZ() + z);
            if (world != null) {
                location.setX(location.getX() + x);
                location.setY(location.getY() + y);
                location.setZ(location.getZ() + z);
            }
            positions.add(origin.clone());
            if (render && world != null) {
                Effects.showCrit(location);
            }
            if (world != null) {
                location.setX(location.getX() - x);
                location.setY(location.getY() - y);
                location.setZ(location.getZ() - z);
            }
            origin.setX(origin.getX() - x);
            origin.setY(origin.getY() - y);
            origin.setZ(origin.getZ() - z);
        }
        return positions;
    }

    public List<Vector> traverse(double blocksAway, double accuracy) {
        return traverse(null, blocksAway, accuracy);
    }

    public Vector positionOfIntersection(Vector min, Vector max, double blocksAway, double accuracy) {
        List<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, min, max)) {
                return position;
            }
        }
        return null;
    }

    public boolean intersects(Vector min, Vector max, double blocksAway, double accuracy) {
        List<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, min, max)) {
                return true;
            }
        }
        return false;
    }

    public Vector positionOfIntersection(BoundingBox boundingBox, double blocksAway, double accuracy) {
        List<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
                return position;
            }
        }
        return null;
    }

    public boolean intersects(BoundingBox boundingBox, double blocksAway, double accuracy) {
        List<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
                return true;
            }
        }
        return false;
    }

    public static boolean intersects(Vector position, BoundingBox boundingBox) {
        return intersects(position, boundingBox.getMin(), boundingBox.getMax());
    }

    public static boolean intersects(Vector position, Vector min, Vector max) {
        if (position.getX() < min.getX() || position.getX() > max.getX()) {
            return false;
        } else if (position.getY() < min.getY() || position.getY() > max.getY()) {
            return false;
        } else return !(position.getZ() < min.getZ()) && !(position.getZ() > max.getZ());
    }
}
