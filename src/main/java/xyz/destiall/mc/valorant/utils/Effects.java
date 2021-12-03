package xyz.destiall.mc.valorant.utils;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.Particles_1_13;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Vector3f;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Effects {
    private static Particles_1_13 PARTICLES;
    private static final Set<Vector> SMOKE_SPHERE = new HashSet<>();
    private static final Set<Vector> FLASH_SPHERE = new HashSet<>();
    private static final Set<Vector> SMOKE_CYLINDER = new HashSet<>();
    private static final Set<Vector> BOMB_SPHERE = new HashSet<>();
    private static final Set<Vector> SPIKE_RING = new HashSet<>();
    public Effects(ParticleNativeAPI api) {
        PARTICLES = api.getParticles_1_13();
        createSmokeSphere();
        createFlashSphere();
        createSmokeCylinder();
        createBombSphere();
        createSpikeRing();
    }

    private void createSpikeRing() {
        SPIKE_RING.clear();
        for (double theta = -Math.PI; theta <= Math.PI; theta += Math.PI / 6) {
            double x = Math.cos(theta);
            double z = Math.sin(theta);
            SPIKE_RING.add(new Vector(x, 0, z));
        }
    }

    private void createBombSphere() {
        BOMB_SPHERE.clear();
        for (double phi = 0; phi <= Math.PI; phi += Math.PI / 36) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 36) {
                double x = Math.cos(theta) * Math.sin(phi);
                double y = Math.cos(phi);
                double z = Math.sin(theta) * Math.sin(phi);
                BOMB_SPHERE.add(new Vector(x, y, z));
            }
        }
    }

    private void createSmokeSphere() {
        SMOKE_SPHERE.clear();
        double r = 2.1;
        for (double phi = 0; phi <= Math.PI; phi += Math.PI / 11) {
            for (double theta = - Math.PI; theta <= Math.PI; theta += Math.PI / 10) {
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
            double prevX = 0;
            double prevZ = 0;
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 10) {
                double x = r * Math.cos(theta) * Math.sin(phi);
                double y = r * Math.cos(phi) - r;
                double z = r * Math.sin(theta) * Math.sin(phi);
                if (prevX == 0 && prevZ == 0) {
                    prevX = x;
                    prevZ = z;
                    continue;
                } else if (Math.pow(x - prevX, 2) + Math.pow(z - prevZ, 2) <= 0.3) continue;
                prevX = x;
                prevZ = z;
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

    public static void bombSphere(Location location, float radius) {
        for (Vector vect : BOMB_SPHERE) {
            double x = vect.getX() * radius;
            double y = vect.getY() * radius;
            double z = vect.getZ() * radius;
            location.add(x, y, z);
            if (!location.getBlock().isEmpty()) {
                location.subtract(x, y, z);
                continue;
            }
            Object dust = PARTICLES.DUST().color(0.1f, 0.1f, 0.1f, 10).packet(true, location);
            PARTICLES.sendPacket(location, 60, dust);
            location.subtract(x, y, z);
        }
    }

    public static void spikeRing(Location location, float radius) {
        for (Vector vect : SPIKE_RING) {
            double x = vect.getX() * radius;
            double z = vect.getZ() * radius;
            location.add(x, 0, z);
            Object dust = PARTICLES.DUST().color(0.0f, 0.5f, 0.5f, 1).packet(true, location);
            PARTICLES.sendPacket(location, 10, dust);
            location.subtract(x, 0, z);
        }
    }

    public static void detonate(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
        Object packet = PARTICLES.EXPLOSION_EMITTER().packet(false, location, 0.5, 1);
        PARTICLES.sendPacket(location, 60, packet);
    }

    public static ScheduledTask smoke(Match match, Location location, Agent type, double duration) {
        final Set<EntityArmorStand> asList = new HashSet<>();
        for (Vector vect : type.equals(Agent.CYPHER) ? SMOKE_CYLINDER : SMOKE_SPHERE) {
            location.add(vect);
            location.setDirection(vect);
            EntityArmorStand as = getSmokeArmorStand(location.clone(), type);
            Effects.sendArmorStand(as, match);
            asList.add(as);
            location.subtract(vect);
        }
        return Scheduler.delay(() -> {
            for (final EntityArmorStand as : asList) {
                removeArmorStand(as, match);
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

    public static ScheduledTask wall(Match match, Location origin, Vector direction, Agent type, double l, double h, double d) {
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
        final Set<EntityArmorStand> asList = new HashSet<>();
        for (Vector vect : locationList) {
            Location loc = origin.clone().add(vect).clone();
            EntityArmorStand as = getSmokeArmorStand(loc, type);
            sendArmorStand(as, match);
            teleportArmorStand(loc, as, match);
            asList.add(as);
        }
        return Scheduler.delay(() -> {
            for (EntityArmorStand as : asList) {
                removeArmorStand(as, match);
            }
            asList.clear();
        }, (long) (d * 20L));
    }

    public static ScheduledTask flash(Player player, Agent type, double duration) {
        Location location = player.getEyeLocation();
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) duration + 2, 1));
        final Set<EntityArmorStand> asList = new HashSet<>();
        for (Vector vect : FLASH_SPHERE) {
            location.add(vect);
            location.setDirection(vect);
            EntityArmorStand as = getFlashArmorStand(location, type);
            sendArmorStand(as, player);
            asList.add(as);
            location.subtract(vect);
        }
        final ScheduledTask task = Scheduler.repeat(() -> {
            for (EntityArmorStand as : asList) {
                Location loc = new Location(player.getWorld(), as.locX(), as.locY(), as.locZ());
                Vector dist = player.getEyeLocation().subtract(loc).toVector();
                teleportArmorStand(loc.add(dist), as, player);
                //as.teleport(as.getLocation().add(dist));
            }
        }, 1L);
        return Scheduler.delay(() -> {
            for (EntityArmorStand as : asList) {
                removeArmorStand(as, player);
            }
            asList.clear();
            Scheduler.cancel(task);
        }, (long) (duration * 20L));
    }

    private static EntityArmorStand createArmorStand(Location location) {
        EntityArmorStand as = new EntityArmorStand(Versioning.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
        as.setNoGravity(true);
        //as.setInvisible(true);
        as.setMarker(true);
        as.setRightArmPose(new Vector3f((float) Math.toRadians(location.getPitch() - 10), 0f, 0f));
        return as;
    }

    public static EntityArmorStand getSmokeArmorStand(Location location, Agent type) {
        EntityArmorStand as = createArmorStand(location);
        as.setSmall(false);
        as.setSlot(EnumItemSlot.f, CraftItemStack.asNMSCopy(new ItemStack(type.WOOL)), true);
        as.setHeadPose(new Vector3f((float)location.getDirection().getX(), (float)location.getDirection().getY(), (float)location.getDirection().getZ()));
        return as;
    }

    public static EntityArmorStand getFlashArmorStand(Location location, Agent type) {
        EntityArmorStand as = getSmokeArmorStand(location, type);
        as.setSmall(true);
        return as;
    }

    public static EntityArmorStand getBladeStormArmorStand(Location location) {
        EntityArmorStand as = createArmorStand(location);
        as.setArms(true);
        as.setSmall(true);
        as.setSlot(EnumItemSlot.a, CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_SWORD)));
        return as;
    }

    public static void removeArmorStand(EntityArmorStand e, Match match) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(e.getId());
        Collection<VPlayer> list = match.getPlayers().values();
        for (VPlayer player : list) {
            PlayerConnection connection = Versioning.getConnection(player.getPlayer());
            connection.sendPacket(packet);
        }
    }

    public static void removeArmorStand(EntityArmorStand e, Player player) {
        PacketPlayOutEntityDestroy packet = Versioning.newDestroyPacket(e.getId());
        PlayerConnection connection = Versioning.getConnection(player);
        connection.sendPacket(packet);
    }

    public static void sendArmorStand(EntityArmorStand e, Match match) {
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(e);
        List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> equipments = new ArrayList<>();
        equipments.add(Pair.of(EnumItemSlot.f, CraftItemStack.asNMSCopy(new ItemStack(Material.BEDROCK))));
        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(e.getId(), equipments);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(e.getId(), e.getDataWatcher(), true);
        Collection<VPlayer> list = match.getPlayers().values();
        for (VPlayer player : list) {
            PlayerConnection connection = Versioning.getConnection(player.getPlayer());
            connection.sendPacket(packet);
            connection.sendPacket(metadata);
            //Scheduler.delay(() -> connection.sendPacket(equip), 2L);
        }
        equipments.clear();
    }

    public static void sendArmorStand(EntityArmorStand e, Player player) {
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(e);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(e.getId(), e.getDataWatcher(), true);
        List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> equipments = new ArrayList<>();
        equipments.add(Pair.of(EnumItemSlot.f, CraftItemStack.asNMSCopy(new ItemStack(Material.BEDROCK))));
        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(e.getId(), equipments);
        PlayerConnection connection = Versioning.getConnection(player);
        connection.sendPacket(packet);
        connection.sendPacket(metadata);
        //Scheduler.delay(() -> connection.sendPacket(equip), 2L);
    }

    public static void teleportArmorStand(Location loc, EntityArmorStand e, Match match) {
        e.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(e);
        Collection<VPlayer> list = match.getPlayers().values();
        for (VPlayer player : list) {
            PlayerConnection connection = Versioning.getConnection(player.getPlayer());
            connection.sendPacket(packet);
        }
    }

    public static void teleportArmorStand(Location loc, EntityArmorStand e, Player player) {
        e.setPosition(loc.getX(), loc.getY(), loc.getZ());
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(e);
        PlayerConnection connection = Versioning.getConnection(player);
        connection.sendPacket(packet);
    }

    public static void disable() {
        SMOKE_SPHERE.clear();
        SMOKE_CYLINDER.clear();
        FLASH_SPHERE.clear();
    }
}
