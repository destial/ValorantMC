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
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Agent;

import java.util.HashSet;
import java.util.Set;

public class Shooter {
    public static void shoot(Player shooter, Location origin, Vector direction, double damage, double spread) {

    }

    public static void snipe(Player shooter, Location origin, Vector direction, double damage, double spread) {
        Location current = origin.clone().add(direction.clone());
        direction.setX(direction.getX() + Math.random() * spread - (spread * 2));
        direction.setY(direction.getY() + Math.random() * spread - (spread * 2));
        direction.setZ(direction.getZ() + Math.random() * spread - (spread * 2));
        final Set<Entity> hitEntities = new HashSet<>();
        boolean isNotHit = true;
        while (isPassable(current.getBlock()) && isNotHit) {
            if (origin.clone().subtract(current.clone()).length() > 100) break;
            current.add(direction.clone());
            Effects.smokeTravel(current.clone(), Agent.JETT);
            hitEntities.addAll(current.getWorld().getNearbyEntities(current.clone(), 1, 1, 1));
            isNotHit = hitEntities.size() == 0;
        }
        for (Entity entity : hitEntities) {
            if (entity instanceof LivingEntity) {
                LivingEntity live = (LivingEntity) entity;
                double dmg = live.getHealth();
                if (current.clone().subtract(live.getEyeLocation().clone()).length() < 1) {
                    dmg = live.getHealth() + live.getAbsorptionAmount();
                }
                EntityDamageEvent e = new EntityDamageEvent(shooter, EntityDamageEvent.DamageCause.ENTITY_ATTACK, dmg);
                live.setLastDamageCause(e);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled()) continue;
                live.damage(dmg);
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
