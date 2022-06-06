package xyz.destiall.mc.valorant.utils;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.events.weapon.SniperShotEvent;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class Shooter {
    public static void shoot(Player shooter, Location origin, Vector direction, double damage, double spread) {
        shoot(shooter, origin, direction, damage, spread, true);
    }

    public static void shoot(Player shooter, Location origin, Vector direction, double damage, double spread, boolean cs) {
        if (Bukkit.getPluginManager().getPlugin("CrackShot") != null && cs) return;

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

        RayTraceResult result = origin.getWorld().rayTrace(origin, dir, 100, FluidCollisionMode.NEVER, true, 0, e -> e instanceof LivingEntity && e != shooter && !(e instanceof ArmorStand));
        LivingEntity live = result != null ? (LivingEntity) result.getHitEntity() : null;

        RayTrace trace = new RayTrace(origin.toVector(), dir);
        trace.setRender(true);
        trace.traverse(origin.getWorld(), result != null ? result.getHitPosition().distance(trace.getOrigin()) : 100, 0.7);

        shooter.getWorld().playSound(shooter.getEyeLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 4, 3);

        Location newLoc = shooter.getLocation();
        Vector vel = shooter.getVelocity();
        newLoc.setDirection(direction.clone().setY(direction.getY() + 0.03));

        if (shooter.teleport(newLoc)) {
            shooter.setGravity(true);
            shooter.setVelocity(vel);
        }

        if (live != null) {
            double dmg = damage;
            if (damage < 0) {
                dmg = 1000;
            }
            EntityDamageEvent e = new EntityDamageByEntityEvent(shooter, live, EntityDamageEvent.DamageCause.CUSTOM, dmg);
            live.setLastDamageCause(e);
            if (dmg >= 1000) {
                boolean headshot = RayTrace.intersects(result.getHitPosition(), BoundingBox.of(live.getEyeLocation(), 0.5, 0.5, 0.5));
                e = new SniperShotEvent(shooter, live, headshot);
                if (live instanceof Player p) {
                    VPlayer vp = MatchManager.getInstance().getPlayer(p);
                    if (vp != null) {
                        vp.setLastDamage(e);
                    }
                }
                Bukkit.getPluginManager().callEvent(e);
            } else {
                Bukkit.getPluginManager().callEvent(e);
            }
            if (e.isCancelled()) return;
            live.damage(dmg, shooter);
        }
    }
}
