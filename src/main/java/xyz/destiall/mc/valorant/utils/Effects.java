package xyz.destiall.mc.valorant.utils;

import com.github.fierioziy.particlenativeapi.api.Particles_1_13;
import com.github.fierioziy.particlenativeapi.api.types.ParticleType;
import com.github.fierioziy.particlenativeapi.plugin.ParticleNativePlugin;
import org.bukkit.Bukkit;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;

import java.util.ArrayList;
import java.util.List;

public class Effects {
    private static Particles_1_13 particles;
    private static final List<Vector> smokeSphere = new ArrayList<>();
    private static final List<Vector> flashSphere = new ArrayList<>();
    private static final List<ArmorStand> spawnedArmorStands = new ArrayList<>();
    public Effects() {
        particles = ParticleNativePlugin.getAPI().getParticles_1_13();
        createSmokeSphere();
        createFlashSphere();
    }
    private void createSmokeSphere() {
        smokeSphere.clear();
        for (double phi = 0; phi <= Math.PI; phi += Math.PI / 10) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 10) {
                double r = 2.1;
                double x = r * Math.cos(theta) * Math.sin(phi);
                double y = r * Math.cos(phi);
                double z = r * Math.sin(theta) * Math.sin(phi);
                smokeSphere.add(new Vector(x, y, z));
            }
        }
    }

    private void createFlashSphere() {
        flashSphere.clear();
        for (double phi = 0; phi <= Math.PI; phi += Math.PI / 7) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 10) {
                double r = 1;
                double x = r * Math.cos(theta) * Math.sin(phi);
                double y = r * Math.cos(phi);
                double z = r * Math.sin(theta) * Math.sin(phi);
                flashSphere.add(new Vector(x, y, z));
            }
        }
    }

    public static void smoke(Location location, Type type, double duration) {
        final List<ArmorStand> asList = new ArrayList<>();
        for (Vector vect : smokeSphere) {
            location.add(vect);
            location.setDirection(vect);
            asList.add(getArmorStand(location, type));
            location.subtract(vect);
        }
        Bukkit.getScheduler().runTaskLater(Valorant.getInstance().getPlugin(), () -> {
            for (ArmorStand as : asList) {
                as.remove();
                spawnedArmorStands.remove(as);
            }
            asList.clear();
        }, (long) (duration * 20L));
    }

    public static void smokeTravel(Location location, Type type) {
        Object packet = particles.DUST_COLOR_TRANSITION().color(type.color, type.color, 2).packet(false, location);
        particles.sendPacket(location, 50D, packet);
    }

    public static void flash(Player player, Type type, double duration) {
        Location location = player.getLocation();
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) duration, 1, false));
        final List<ArmorStand> asList = new ArrayList<>();
        for (Vector vect : flashSphere) {
            location.add(vect);
            location.setDirection(vect);
            asList.add(getArmorStand(location, type));
            location.subtract(vect);
        }
        final BukkitTask task = Bukkit.getScheduler().runTaskTimer(Valorant.getInstance().getPlugin(), () -> {
            for (ArmorStand as : asList) {
                Vector dist = player.getLocation().subtract(as.getLocation()).toVector();
                as.teleport(as.getLocation().add(dist));
            }
        }, 0L, 1L);
        Bukkit.getScheduler().runTaskLater(Valorant.getInstance().getPlugin(), () -> {
            for (ArmorStand as : asList) {
                as.remove();
                spawnedArmorStands.remove(as);
            }
            asList.clear();
            task.cancel();
        }, (long) (duration * 20L));
    }

    private static ArmorStand getArmorStand(Location location, Type type) {
        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        as.setArms(false);
        as.setBasePlate(false);
        as.setMarker(false);
        as.setSmall(false);
        as.setVisible(false);
        as.setGravity(false);
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
        as.getEquipment().setHelmet(new ItemStack(type.wool));
        spawnedArmorStands.add(as);
        return as;
    }

    public static void disable() {
        for (ArmorStand as : spawnedArmorStands) {
            as.remove();
        }
        spawnedArmorStands.clear();
        smokeSphere.clear();
        flashSphere.clear();
    }

    public enum Type {
        JETT(Color.AQUA),
        REYNA(Color.PURPLE);
        Color color;
        Material wool;
        Type(Color color) {
            this.color = color;
            wool = Material.WHITE_WOOL;
            if (color.toString().equals(Color.AQUA.toString())) {
                wool = Material.CYAN_WOOL;
            } else if (color.toString().equals(Color.PURPLE.toString())) {
                wool = Material.PURPLE_WOOL;
            }
        }
    }
}
