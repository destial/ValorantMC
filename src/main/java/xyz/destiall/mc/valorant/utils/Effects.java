package xyz.destiall.mc.valorant.utils;

import com.comphenix.protocol.ProtocolManager;
import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.Particles_1_13;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class Effects {
    private static ParticleNativeAPI PARTICLES_API;
    private static ProtocolManager PROTOCOL_LIB;
    private static Particles_1_13 PARTICLES;
    private static final Set<Vector> SMOKE_SPHERE = new HashSet<>();
    private static final Set<Vector> FLASH_SPHERE = new HashSet<>();
    private static final Set<Vector> SMOKE_CYLINDER = new HashSet<>();
    private static final Set<ArmorStand> SPAWNED_ARMOR_STANDS = new HashSet<>();
    public Effects(ParticleNativeAPI api, ProtocolManager pm) {
        Effects.PARTICLES_API = api;
        Effects.PROTOCOL_LIB = pm;
        PARTICLES = api.getParticles_1_13();
        createSmokeSphere();
        createFlashSphere();
        createSmokeCylinder();
    }
    private void createSmokeSphere() {
        SMOKE_SPHERE.clear();
        double r = 2.1;
        for (double phi = 0; phi <= Math.PI; phi += Math.PI / 11) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 10) {
                double x = r * Math.cos(theta) * Math.sin(phi);
                double y = r * Math.cos(phi);
                double z = r * Math.sin(theta) * Math.sin(phi);
                SMOKE_SPHERE.add(new Vector(x, y, z));
            }
        }
    }

    private void createSmokeCylinder() {
        SMOKE_CYLINDER.clear();
        double r = 3.1;
        double h = 2.1;
        for (double height = 0; height < h; height += Math.PI / 5) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 15) {
                double x = r * Math.cos(theta);
                double z = r * Math.sin(theta);
                SMOKE_CYLINDER.add(new Vector(x, height, z));
            }
        }
    }

    private void createFlashSphere() {
        FLASH_SPHERE.clear();
        double r = 1;
        for (double phi = 0; phi <= Math.PI; phi += Math.PI / 7) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 10) {
                double x = r * Math.cos(theta) * Math.sin(phi);
                double y = r * Math.cos(phi) - r;
                double z = r * Math.sin(theta) * Math.sin(phi);
                FLASH_SPHERE.add(new Vector(x, y, z));
            }
        }
    }

    public static void shockDart(Location location) {
        for (Vector vect : SMOKE_SPHERE) {
            location.add(vect);
            smokeTravel(location, Agent.SOVA);
            location.subtract(vect);
        }
    }

    public static ScheduledTask smoke(Location location, Agent type, double duration) {
        final Set<ArmorStand> asList = new HashSet<>();
        for (Vector vect : type.equals(Agent.CYPHER) ? SMOKE_CYLINDER : SMOKE_SPHERE) {
            location.add(vect);
            location.setDirection(vect);
            asList.add(getArmorStand(location, type));
            location.subtract(vect);
        }
        return Scheduler.delay(() -> {
            for (final ArmorStand as : asList) {
                as.remove();
                SPAWNED_ARMOR_STANDS.remove(as);
            }
            asList.clear();
        }, (long) (duration * 20L));
    }

    public static void dartTravel(Location location, @Nullable VPlayer VPlayer) {
        Object packet = PARTICLES.DUST_COLOR_TRANSITION().color(Color.BLUE, Color.BLUE, 1).packet(false, location);
        if (VPlayer != null) {
            for (VPlayer own : VPlayer.getTeam().getMembers()) {
                PARTICLES.sendPacket(own.getPlayer(), packet);
            }
            packet = PARTICLES.DUST_COLOR_TRANSITION().color(Color.RED, Color.RED, 1).packet(false, location);
            Team otherTeam = VPlayer.getMatch().getTeams().stream().filter(t -> t != VPlayer.getTeam()).findFirst().orElse(null);
            if (otherTeam != null) {
                for (VPlayer enemy : otherTeam.getMembers()) {
                    PARTICLES.sendPacket(enemy.getPlayer(), packet);
                }
            }
        } else {
            PARTICLES.sendPacket(location, 50D, packet);
        }
    }

    public static void bullet(Location location) {
        Object packet = PARTICLES.DUST_COLOR_TRANSITION().color(Color.RED, Color.RED, 1).packet(false, location);
        PARTICLES.sendPacket(location, 150D, packet);
    }

    public static void smokeTravel(Location location, Agent type) {
        Object packet = PARTICLES.DUST_COLOR_TRANSITION().color(type.COLOR, type.COLOR, 2).packet(false, location);
        PARTICLES.sendPacket(location, 50D, packet);
    }

    public static void flashTravel(Location location, Agent type) {
        Object packet = PARTICLES.DUST_COLOR_TRANSITION().color(type.COLOR, type.COLOR, 2).packet(false, location);
        PARTICLES.sendPacket(location, 50D, packet);
    }

    public static ScheduledTask wall(Location origin, Vector direction, Agent type, double l, double h, double d) {
        final Vector dir = direction.clone().normalize();
        final Set<Vector> locationList = new HashSet<>();
        for (double i = 0; i <= l; i += 0.6) {
            Vector vect = new Vector(-dir.getX() * i, 0, -dir.getZ() * i);
            Location location = origin.clone().subtract(0, 2, 0).add(vect);
            while (location.getBlock().isEmpty() && location.getY() >= 0) {
                location.subtract(0, 1, 0);
            }
            location.subtract(0, 2, 0);
            Vector vectt = origin.clone().subtract(location).toVector();
            for (double j = 0; j <= h; j += 0.6) {
                vectt.setY(j);
                locationList.add(vectt.clone());
            }
        }
        final Set<ArmorStand> asList = new HashSet<>();
        for (Vector vect : locationList) {
            Location loc = origin.clone().add(vect).clone();
            ArmorStand as = getArmorStand(loc, type);
            as.teleport(loc);
            asList.add(as);
        }
        return Scheduler.delay(() -> {
            for (ArmorStand as : asList) {
                SPAWNED_ARMOR_STANDS.remove(as);
                as.remove();
            }
            asList.clear();
        }, (long) (d * 20L));
    }

    public static ScheduledTask flash(Player player, Agent type, double duration) {
        Location location = player.getEyeLocation();
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) duration + 2, 1));
        final Set<ArmorStand> asList = new HashSet<>();
        for (Vector vect : FLASH_SPHERE) {
            location.add(vect);
            location.setDirection(vect);
            asList.add(getSmallArmorStand(location, type));
            location.subtract(vect);
        }
        final ScheduledTask task = Scheduler.repeat(() -> {
            for (ArmorStand as : asList) {
                Vector dist = player.getEyeLocation().subtract(as.getLocation()).toVector();
                as.teleport(as.getLocation().add(dist));
            }
        }, 1L);
        return Scheduler.delay(() -> {
            for (ArmorStand as : asList) {
                as.remove();
                SPAWNED_ARMOR_STANDS.remove(as);
            }
            asList.clear();
            Scheduler.cancel(task);
        }, (long) (duration * 20L));
    }

    private static ArmorStand createArmorStand(Location location) {
        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        SPAWNED_ARMOR_STANDS.add(as);
        as.setBasePlate(false);
        as.setMarker(false);
        as.setSmall(false);
        as.setVisible(false);
        as.setGravity(false);
        as.setNoDamageTicks(0);
        as.setMaximumNoDamageTicks(0);
        as.setCollidable(false);
        as.setInvulnerable(true);
        as.setInvisible(true);
        as.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.REMOVING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.REMOVING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.REMOVING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
        as.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        return as;
    }

    public static ArmorStand getArmorStand(Location location, Agent type) {
        ArmorStand as = createArmorStand(location);
        as.setSmall(false);
        as.setArms(false);
        as.getEquipment().setHelmet(new ItemStack(type.WOOL));
        return as;
    }

    public static ArmorStand getSmallArmorStand(Location location, Agent type) {
        ArmorStand as = getArmorStand(location, type);
        as.setSmall(true);
        return as;
    }

    public static ArmorStand getBladeStormArmorStand(Location location) {
        ArmorStand as = createArmorStand(location);
        as.setArms(true);
        as.setSmall(true);
        as.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
        return as;
    }

    public static void disable() {
        for (ArmorStand as : SPAWNED_ARMOR_STANDS) {
            as.remove();
        }
        SPAWNED_ARMOR_STANDS.clear();
        SMOKE_SPHERE.clear();
        SMOKE_CYLINDER.clear();
        FLASH_SPHERE.clear();
    }
}
