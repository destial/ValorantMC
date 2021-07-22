package xyz.destiall.mc.valorant.utils;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Shooter {
    private static final Map<LivingEntity, ScheduledTask> TICKS = new HashMap<>();
    public static void shoot(Player shooter, Location origin, Vector direction, double damage, double spread) {
        Location current = origin.clone().add(direction.clone());
        Vector dir = direction.clone();
        dir.setX(direction.getX() + (Math.random() * spread - spread * 0.5) * 0.1);
        dir.setY(direction.getY() + (Math.random() * spread - spread * 0.5) * 0.1);
        dir.setZ(direction.getZ() + (Math.random() * spread - spread * 0.5) * 0.1);
        final Set<Entity> hitEntities = new HashSet<>();
        while (isPassable(current.getBlock())) {
            if (origin.clone().subtract(current.clone()).length() > 100) break;
            current.add(dir);
            Effects.bullet(current.clone());
            hitEntities.addAll(current.getWorld().getNearbyEntities(current.clone(), 1, 1, 1).stream().filter(e -> e.getBoundingBox().contains(current.clone().toVector())).collect(Collectors.toList()));
        }
        for (Entity entity : hitEntities) {
            if (entity instanceof LivingEntity) {
                LivingEntity live = (LivingEntity) entity;
                double dmg = damage;
                if (current.clone().subtract(live.getEyeLocation().clone()).length() < 1) {
                    dmg = 100;
                }
                EntityDamageEvent e = new EntityDamageEvent(shooter, EntityDamageEvent.DamageCause.ENTITY_ATTACK, dmg);
                live.setLastDamageCause(e);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled()) continue;
                live.damage(dmg);
                int prevTicks = live.getNoDamageTicks();
                live.setNoDamageTicks(0);
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

    public static void snipe(Player shooter, Location origin, Vector direction, double damage, double spread) {
        Location current = origin.clone().add(direction.clone());
        Vector dir = direction.clone();
        dir.setX(direction.getX() + (Math.random() * spread - spread * 0.5) * 0.1);
        dir.setY(direction.getY() + (Math.random() * spread - spread * 0.5) * 0.1);
        dir.setZ(direction.getZ() + (Math.random() * spread - spread * 0.5) * 0.1);
        final Set<Entity> hitEntities = new HashSet<>();
        while (isPassable(current.getBlock())) {
            if (origin.clone().subtract(current.clone()).length() > 100) break;
            current.add(dir);
            Effects.bullet(current.clone());
            hitEntities.addAll(current.getWorld().getNearbyEntities(current.clone(), 1, 1, 1).stream().filter(e -> e.getBoundingBox().contains(current.clone().toVector())).collect(Collectors.toList()));
        }
        shooter.getLocation().getWorld().playSound(shooter.getEyeLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 5, 3);
        for (Entity entity : hitEntities) {
            if (entity instanceof LivingEntity) {
                LivingEntity live = (LivingEntity) entity;
                double dmg = damage;
                if (current.clone().subtract(live.getEyeLocation().clone()).length() < 1) {
                    dmg = 10000;
                }
                EntityDamageEvent e = new EntityDamageEvent(shooter, EntityDamageEvent.DamageCause.ENTITY_ATTACK, dmg);
                live.setLastDamageCause(e);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled()) continue;
                live.damage(dmg);
                int prevTicks = live.getNoDamageTicks();
                live.setNoDamageTicks(0);
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

    private static boolean isPassable(Block block) {
        Material type = block.getType();
        return (block.isPassable() || block.isEmpty()) && (type.isAir() || !type.isSolid());
    }
}
