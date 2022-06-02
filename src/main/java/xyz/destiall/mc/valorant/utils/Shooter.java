package xyz.destiall.mc.valorant.utils;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Shooter {
    private static final Map<LivingEntity, ScheduledTask> TICKS = new HashMap<>();
    public static void shoot(Player shooter, Location origin, Vector direction, double damage, double spread) {
        if (!shooter.isOnline()) return;
        Location current = new Location(origin.getWorld(), origin.getX(), origin.getY(), origin.getZ(), origin.getYaw(), origin.getPitch());
        current.add(direction);
        Vector dir = direction.clone();
        if (!shooter.isOnGround()) {
            spread *= 1.5;
        }
        dir.setX(direction.getX() + (Math.random() * spread - spread * 0.5) * 0.1);
        dir.setY(direction.getY() + (Math.random() * spread - spread * 0.5) * 0.1);
        dir.setZ(direction.getZ() + (Math.random() * spread - spread * 0.5) * 0.1);
        final Set<Entity> hitEntities = new HashSet<>();
        while (isPassable(current.getBlock(), dir)) {
            if (origin.distanceSquared(current) > (100 * 100)) break;
            current.add(dir);
            Effects.bullet(current);
            Collection<Entity> entities = new ArrayList<>();
            try {
                entities = shooter.getWorld().getNearbyEntities(BoundingBox.of(current, 0.5d, 0.5d, 0.5d));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Iterator<Entity> it = entities.stream().filter(e -> e.getBoundingBox().contains(current.toVector())).iterator();
            while (it.hasNext()) {
                hitEntities.add(it.next());
            }
        }
        shooter.getWorld().playSound(shooter.getEyeLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 3, 3);
        shooter.getWorld().playSound(current, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 3, 3);
        Location newLoc = shooter.getLocation();
        Vector vel = shooter.getVelocity();
        newLoc.setDirection(direction.clone().setY(direction.getY() + 0.03));
        if (shooter.teleport(newLoc)) {
            shooter.setGravity(true);
            shooter.setVelocity(vel);
        }
        for (Entity entity : hitEntities) {
            if (entity instanceof ArmorStand) continue;
            if (entity == shooter) continue;
            if (entity instanceof LivingEntity live) {
                double dmg = damage;
                if (damage < 0) {
                    dmg = 1000;
                }
                if (current.clone().subtract(live.getEyeLocation()).length() < 1) {
                    dmg *= 2;
                }
                EntityDamageEvent e = new EntityDamageEvent(shooter, EntityDamageEvent.DamageCause.CUSTOM, dmg);
                live.setLastDamageCause(e);
                Bukkit.getPluginManager().callEvent(e);
                int prevTicks = live.getNoDamageTicks();
                live.setNoDamageTicks(0);
                if (e.isCancelled()) continue;
                live.damage(dmg);
                ScheduledTask t = Scheduler.delay(() -> {
                    live.setNoDamageTicks(prevTicks);
                    TICKS.remove(live);
                }, prevTicks);
                if (TICKS.get(live) != null) {
                    TICKS.get(live).cancel();
                }
                TICKS.put(live, t);
            } else {
                entity.getWorld().playEffect(entity.getLocation(), Effect.STEP_SOUND, 1);
                entity.remove();
            }
        }
    }

    private static boolean isPassable(Block block, Vector dir) {
        Material type = block.getType();
        return (block.isPassable() || block.isEmpty()) && (type.isAir() || !type.isSolid());
    }
}
