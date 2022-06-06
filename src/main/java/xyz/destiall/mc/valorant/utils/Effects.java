package xyz.destiall.mc.valorant.utils;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.Particles_1_13;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Rotations;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.craftbukkit.v1_18_R2.CraftSound;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.player.VPlayer;

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

    public static void showDust(VPlayer player, Location location, Color color) {
        Object dust = PARTICLES.DUST().color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 2).packet(true, location);
        PARTICLES.sendPacket(player.getPlayer(), dust);
    }

    public static void showDust(Location location, Color color) {
        Object dust = PARTICLES.DUST().color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 2).packet(true, location);
        PARTICLES.sendPacket(location, 50, dust);
    }

    public static void walking(VPlayer player) {
        Location location = player.getLocation();
        Match match = player.getMatch();
        for (VPlayer p : match.getPlayers().values()) {
            if (p == player) continue;
            sendSound(location, p.getPlayer(), Sound.BLOCK_STONE_STEP, 5, 1);
        }
    }

    public static void sendSound(Location location, Player player, Sound sound, float volume, float pitch) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        SoundEvent soundEvent = CraftSound.getSoundEffect(sound);
        SoundSource soundSource = SoundSource.valueOf(SoundCategory.MASTER.name());
        ClientboundSoundPacket packet = new ClientboundSoundPacket(soundEvent, soundSource, x, y, z, volume, pitch);
        Connection connection = Versioning.getConnection(player.getPlayer());
        connection.send(packet);
    }

    public static void showCrit(Location location) {
        Object dust = PARTICLES.CRIT().packet(true, location);
        PARTICLES.sendPacket(location, 50, dust);
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
            Object dust = PARTICLES.DUST().color(0.0f, 0.5f, 0.5f, 2).packet(true, location);
            PARTICLES.sendPacket(location, 10, dust);
            location.subtract(x, 0, z);
        }
    }

    public static void detonate(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10f, 1f);
        Object packet = PARTICLES.EXPLOSION_EMITTER().packet(false, location, 0.5, 1);
        PARTICLES.sendPacket(location, 60, packet);
    }

    public static ScheduledTask smoke(Match match, Location location, Agent type, double duration) {
        final Set<ArmorStand> asList = new HashSet<>();
        for (Vector vect : type.equals(Agent.CYPHER) ? SMOKE_CYLINDER : SMOKE_SPHERE) {
            location.add(vect);
            location.setDirection(vect);
            ArmorStand as = getSmokeArmorStand(location.clone(), type);
            sendArmorStand(as, match, type);
            asList.add(as);
            location.subtract(vect);
        }
        return Scheduler.delay(() -> {
            for (final ArmorStand as : asList) {
                removeArmorStand(as, match);
            }
            asList.clear();
        }, (long) (duration * 20L));
    }

    public static void dartTravel(Location location, VPlayer VPlayer) {
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
        Object packet = PARTICLES.DUST_COLOR_TRANSITION().color(type.COLOR, type.COLOR, 4).packet(false, location);
        PARTICLES.sendPacket(location, 50D, packet);
    }

    public static ScheduledTask wall(Match match, Location origin, Vector direction, Agent type, double l, double h, double d) {
        final Vector dir = direction.normalize();
        final Set<Vector> locationList = new HashSet<>();
        origin.subtract(0, 2, 0);
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
            ArmorStand as = getSmokeArmorStand(loc, type);
            sendArmorStand(as, match, type);
            teleportArmorStand(loc, as, match);
            asList.add(as);
        }
        return Scheduler.delay(() -> {
            for (ArmorStand as : asList) {
                removeArmorStand(as, match);
            }
            asList.clear();
        }, (long) (d * 20L));
    }

    public static ScheduledTask flash(VPlayer player, Agent type, double duration) {
        Location location = player.getEyeLocation();
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) duration + 2, 1));
        final Set<ArmorStand> asList = new HashSet<>();
        for (Vector vect : FLASH_SPHERE) {
            location.add(vect);
            location.setDirection(vect);
            ArmorStand as = getFlashArmorStand(location, type);
            sendArmorStand(as, player);
            asList.add(as);
            location.subtract(vect);
        }
        final ScheduledTask task = Scheduler.repeat(() -> {
            for (ArmorStand as : asList) {
                Location loc = new Location(player.getPlayer().getWorld(), as.getX(), as.getY(), as.getZ());
                Vector dist = player.getEyeLocation().subtract(as.getX(), as.getY(), as.getZ()).toVector();
                teleportArmorStand(loc.add(dist), as, player.getPlayer());
            }
        }, 1L);
        return Scheduler.delay(() -> {
            for (ArmorStand as : asList) {
                removeArmorStand(as, player.getPlayer());
            }
            asList.clear();
            Scheduler.cancel(task);
        }, (long) (duration * 20L));
    }

    public static ArmorStand createPickupName(Item item) {
        ArmorStand as = createArmorStand(item.getLocation());
        as.setCustomNameVisible(true);
        as.setCustomName(new TextComponent("Press 'F' to pick up"));
        return as;
    }

    public static ArmorStand createArmorStand(Location location) {
        ArmorStand as = new ArmorStand(Versioning.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
        as.setNoGravity(true);
        as.setInvisible(true);
        as.setMarker(true);
        as.setRightArmPose(new Rotations((float) Math.toRadians(location.getPitch() - 10), 0f, 0f));
        as.setSmall(false);
        as.persist = true;
        as.persistentInvisibility = true;
        as.noCulling = true;
        return as;
    }

    public static ArmorStand getSmokeArmorStand(Location location, Agent type) {
        ArmorStand as = createArmorStand(location);
        as.setItemSlot(EquipmentSlot.HEAD, Versioning.getItemStack(new ItemStack(type.WOOL, 1)));
        as.setHeadPose(new Rotations((float)location.getDirection().getX(), (float)location.getDirection().getY(), (float)location.getDirection().getZ()));
        return as;
    }

    public static ArmorStand getFlashArmorStand(Location location, Agent type) {
        ArmorStand as = getSmokeArmorStand(location, type);
        as.setSmall(true);
        return as;
    }

    public static ArmorStand getBladeStormArmorStand(Location location) {
        ArmorStand as = createArmorStand(location);
        as.setShowArms(true);
        as.setSmall(true);
        as.setItemSlot(EquipmentSlot.MAINHAND, Versioning.getItemStack(new ItemStack(Material.DIAMOND_SWORD)));
        return as;
    }

    public static void removeArmorStand(ArmorStand e, Match match) {
        ClientboundRemoveEntitiesPacket packet = Versioning.newDestroyPacket(e.getId());
        Collection<VPlayer> list = match.getPlayers().values();
        for (VPlayer player : list) {
            Connection connection = Versioning.getConnection(player.getPlayer());
            connection.send(packet);
        }
    }

    public static void removeArmorStand(ArmorStand e, Player player) {
        ClientboundRemoveEntitiesPacket packet = Versioning.newDestroyPacket(e.getId());
        Connection connection = Versioning.getConnection(player);
        connection.send(packet);
    }

    public static void sendArmorStand(ArmorStand e, Match match, Agent agent) {
        sendArmorStand(e, match, EquipmentSlot.HEAD, new ItemStack(agent.WOOL));
    }

    public static void sendArmorStand(ArmorStand e, Match match, ItemStack item) {
        sendArmorStand(e, match, EquipmentSlot.HEAD, item);
    }

    public static void sendArmorStand(ArmorStand e, Match match, EquipmentSlot slot, ItemStack item) {
        ClientboundAddEntityPacket packet = new  ClientboundAddEntityPacket(e);
        ClientboundSetEquipmentPacket equip = new ClientboundSetEquipmentPacket(e.getId(), List.of(Pair.of(slot, Versioning.getItemStack(item))));
        ClientboundSetEntityDataPacket metadata = new ClientboundSetEntityDataPacket(e.getId(), e.getEntityData(), true);
        Collection<VPlayer> list = match.getPlayers().values();
        for (VPlayer player : list) {
            Connection connection = Versioning.getConnection(player.getPlayer());
            connection.send(packet);
            connection.send(metadata);
            Scheduler.delay(() -> connection.send(equip), 2L);
        }
    }

    public static void sendArmorStand(ArmorStand e, Player player) {
        ClientboundAddEntityPacket packet = new  ClientboundAddEntityPacket(e);
        ClientboundSetEntityDataPacket metadata = new ClientboundSetEntityDataPacket(e.getId(), e.getEntityData(), true);
        Connection connection = Versioning.getConnection(player.getPlayer());
        connection.send(packet);
        connection.send(metadata);
    }

    public static void sendArmorStand(ArmorStand e, VPlayer player) {
        ClientboundAddEntityPacket packet = new  ClientboundAddEntityPacket(e);
        ClientboundSetEquipmentPacket equip = new ClientboundSetEquipmentPacket(e.getId(), List.of(Pair.of(EquipmentSlot.HEAD, Versioning.getItemStack(new ItemStack(player.getAgent().WOOL)))));
        ClientboundSetEntityDataPacket metadata = new ClientboundSetEntityDataPacket(e.getId(), e.getEntityData(), true);
        Connection connection = Versioning.getConnection(player.getPlayer());
        connection.send(packet);
        connection.send(metadata);
        Scheduler.delay(() -> connection.send(equip), 2L);
    }

    public static void teleportArmorStand(Location loc, ArmorStand e, Match match) {
        e.setPos(loc.getX(), loc.getY(), loc.getZ());
        e.setYRot(loc.getYaw());
        Rotations rot = new Rotations((float) Math.toRadians(loc.getPitch() - 10), 0f, 0f);
        e.setRightArmPose(rot);
        ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(e);
        Collection<VPlayer> list = match.getPlayers().values();
        for (VPlayer player : list) {
            Connection connection = Versioning.getConnection(player.getPlayer());
            connection.send(packet);
        }
    }

    public static void teleportArmorStand(Location loc, ArmorStand e, Player player) {
        e.setPos(loc.getX(), loc.getY(), loc.getZ());
        e.setYRot(loc.getYaw());
        Rotations rot = new Rotations((float) Math.toRadians(loc.getPitch() - 10), 0f, 0f);
        e.setRightArmPose(rot);
        ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(e);
        Connection connection = Versioning.getConnection(player.getPlayer());
        connection.send(packet);
    }

    public static void disable() {
        SMOKE_SPHERE.clear();
        SMOKE_CYLINDER.clear();
        FLASH_SPHERE.clear();
    }
}
