package xyz.destiall.mc.valorant.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Versioning {
    private static Class<?> craftPlayerClass;
    private static Class<?> craftWorldClass;
    private static Method getProfile;
    private static Method getWorldHandle;
    private static Method getPlayerHandle;

    static {
        try {
            String version = Bukkit.getServer().getClass().toString().split("\\.")[3];
            craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            craftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
            getProfile = craftPlayerClass.getDeclaredMethod("getProfile");
            getProfile.setAccessible(true);
            getWorldHandle = craftWorldClass.getDeclaredMethod("getHandle");
            getWorldHandle.setAccessible(true);
            getPlayerHandle = craftPlayerClass.getDeclaredMethod("getHandle");
            getPlayerHandle.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AtomicInteger getNextEntityIdAtomic() {
        try {
            Field entityCount = Entity.class.getDeclaredField("b");
            entityCount.setAccessible(true);
            AtomicInteger id = (AtomicInteger) entityCount.get(null);
            id.incrementAndGet();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return new AtomicInteger((int) Math.round(Math.random() * Integer.MAX_VALUE * 0.25));
        }
    }

    @SuppressWarnings("all")
    public static DataWatcher clonePlayerDatawatcher(Player player, int currentEntId) {
        Location loc = player.getLocation();
        EntityHuman h = new EntityHuman(getWorld(player.getWorld()), new BlockPosition(loc.getX(),loc.getY(),loc.getZ()), loc.getYaw(), getGameProfile(player)) {
            public BlockPosition getChunkCoordinates() {
                return null;
            }
            public boolean isSpectator() {
                return false;
            }
            @Override
            public boolean isCreative() {
                return false;
            }
        };
        h.e(currentEntId);
        return h.getDataWatcher();
    }

    public static World getWorld(org.bukkit.World world) {
        try {
            return (World) getWorldHandle.invoke(craftWorldClass.cast(world));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("all")
    public static WorldServer getWorldServer(org.bukkit.World world) {
        return getWorld(world).getMinecraftWorld();
    }

    @SuppressWarnings("all")
    public static MinecraftServer getMinecraftServer(org.bukkit.World world) {
        return getWorld(world).getMinecraftServer();
    }

    public static EntityPlayer getPlayer(Player player) {
        try {
            return (EntityPlayer) getPlayerHandle.invoke(craftPlayerClass.cast(player.getPlayer()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("all")
    public static PlayerConnection getConnection(Player player) {
        return getPlayer(player).b;
    }

    public static GameProfile getGameProfile(Player player) {
        try {
            return (GameProfile) getProfile.invoke(craftPlayerClass.cast(player.getPlayer()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new GameProfile(player.getUniqueId(), player.getName());
    }


    public static Location getNonClippableBlockUnderPlayer(Location loc, int addToYPos) {
        if (loc.getBlockY() < 0) return null;
        for (int y = loc.getBlockY(); y >= 0; y--) {
            @SuppressWarnings("all")
            Material m = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getType();
            if (m.isSolid()) {
                return new Location(loc.getWorld(), loc.getX(), y + addToYPos, loc.getZ(), loc.getYaw(), loc.getPitch());
            }
        }
        return null;
    }

    public static GameProfile cloneProfileWithRandomUUID(GameProfile oldProf, String name) {
        GameProfile newProf = new GameProfile(UUID.randomUUID(), name);
        newProf.getProperties().putAll(oldProf.getProperties());
        return newProf;
    }

    @SuppressWarnings("all")
    public static PacketPlayOutEntityDestroy newDestroyPacket(int entityId) {
        try {
            return PacketPlayOutEntityDestroy.class.getConstructor(int.class).newInstance(entityId);
        } catch (Exception ignored) {
            // e.printStackTrace();
        }
        return new PacketPlayOutEntityDestroy();
    }
}
