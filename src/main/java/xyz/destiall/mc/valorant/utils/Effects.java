package xyz.destiall.mc.valorant.utils;

import com.github.fierioziy.particlenativeapi.api.Particles_1_13;
import com.github.fierioziy.particlenativeapi.api.types.ParticleType;
import com.github.fierioziy.particlenativeapi.plugin.ParticleNativePlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;

public class Effects {
    private static Particles_1_13 particles;
    public Effects() {
        particles = ParticleNativePlugin.getAPI().getParticles_1_13();
    }

    public static void smoke(Location location, Type type, Match match) {
        for (double i = 0; i <= Math.PI; i += Math.PI / 20) {
            double radius = Math.sin(i);
            double y = Math.cos(i);
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / 20) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                location.add(x, y, z);
                for (Participant participant : match.getPlayers().values()) {
                    ParticleType pType = particles.DUST_COLOR_TRANSITION().color(type.color, type.color, 1);
                    particles.sendPacket(participant.getPlayer(), pType);
                    participant.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 1));
                }
                location.subtract(x, y, z);
            }
        }
    }

    public static void flash(Participant participant, Type type) {
        Location location = participant.getPlayer().getEyeLocation();
        ParticleType pType = particles.DUST_COLOR_TRANSITION().color(type.color, type.color, 1);
        pType.packet(false, location);
    }

    private static ArmorStand getArmorStand(Location location, Type type, Match match) {
        ArmorStand as = (ArmorStand) match.getMap().getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        as.setArms(false);
        as.setBasePlate(false);
        as.setMarker(false);
        as.setSmall(false);
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
        as.getEquipment().setHelmet(new ItemStack(Material.WHITE_WOOL));
        return as;
    }

    public enum Type {
        JETT(Color.AQUA),
        REYNA(Color.PURPLE);
        Color color;
        Type(Color color) {
            this.color = color;
        }
    }
}
