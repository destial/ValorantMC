package xyz.destiall.mc.valorant.api.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.match.Match;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DeadBody {
    private static final HashMap<VPlayer, DeadBody> DEAD_BODIES = new HashMap<>();
    private int entityId;
    private GameProfile profile;
    private final Location location;
    private final VPlayer player;
    private static Class<?> craftPlayerClass;
    private static Class<?> craftWorldClass;
    private static Method getProfile;
    private static Method getWorldHandle;
    static {
        try {
            String version = Bukkit.getServer().getClass().toString().split("\\.")[3];
            craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            craftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
            getProfile = craftPlayerClass.getDeclaredMethod("getProfile");
            getProfile.setAccessible(true);
            getWorldHandle = craftWorldClass.getDeclaredMethod("getHandle");
            getWorldHandle.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DeadBody(VPlayer player) {
        this.player = player;
        this.location = player.getLocation().clone();
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

    public static DataWatcher clonePlayerDatawatcher(Player player, int currentEntId) {
        Location loc = player.getLocation();
        EntityHuman h = new EntityHuman(getWorld(player.getWorld()), new BlockPosition(loc.getX(),loc.getY(),loc.getZ()), loc.getYaw(), getGameprofile(player)) {
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

    public static GameProfile getGameprofile(Player player) {
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

    public void die() {
        entityId = getNextEntityIdAtomic().get();
        profile = cloneProfileWithRandomUUID(getGameprofile(player.getPlayer()), "");
        DataWatcher dw = clonePlayerDatawatcher(player.getPlayer(), entityId);
        DataWatcherObject<Byte> skinFlags = new DataWatcherObject<>(16, DataWatcherRegistry.a);
        dw.set(skinFlags, (byte)0x7F);
        Location locUnder = getNonClippableBlockUnderPlayer(location, 1);
        Location used = locUnder != null ? locUnder : location;
        player.setDead(true);
        DEAD_BODIES.put(player, this);
        spawn(used);
    }

    private void makePlayerSleep(PlayerConnection conn, BlockPosition bedPos) {
        EntityPlayer entityPlayer = new EntityPlayer(((CraftWorld) player.getPlayer().getWorld()).getHandle().getMinecraftServer(), ((CraftWorld) player.getPlayer().getWorld()).getHandle(), profile);
        entityPlayer.e(entityId);
        try {
            Field poseF = Entity.class.getDeclaredField("ad");
            poseF.setAccessible(true);
            DataWatcherObject<EntityPose> POSE = (DataWatcherObject<EntityPose>) poseF.get(null);
            entityPlayer.getDataWatcher().set(POSE, EntityPose.c);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        entityPlayer.entitySleep(bedPos); //go to sleep
        conn.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), entityPlayer.getDataWatcher(), false));
    }

    public void spawn(Location location) {
        PacketPlayOutEntity.PacketPlayOutRelEntityMove movePacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(entityId, (short) (0), (short) (-61.8), (short) (0), false);
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player.getPlayer()).getHandle());
        try {
            Field a = spawnPacket.getClass().getDeclaredField("a");
            a.setAccessible(true);
            a.set(spawnPacket, entityId);
            Field b = spawnPacket.getClass().getDeclaredField("b");
            b.setAccessible(true);
            b.set(spawnPacket, profile.getId());
            Field c = spawnPacket.getClass().getDeclaredField("c");
            c.setAccessible(true);
            c.setDouble(spawnPacket, location.getX());
            Field d = spawnPacket.getClass().getDeclaredField("d");
            d.setAccessible(true);
            d.setDouble(spawnPacket, location.getY()+ 1.0f/16.0f);
            Field e = spawnPacket.getClass().getDeclaredField("e");
            e.setAccessible(true);
            e.setDouble(spawnPacket, location.getZ());
            Field f = spawnPacket.getClass().getDeclaredField("f");
            f.setAccessible(true);
            f.setByte(spawnPacket, (byte) (int) (location.getYaw() * 256.0F / 360.0F));
            Field g = spawnPacket.getClass().getDeclaredField("g");
            g.setAccessible(true);
            g.setByte(spawnPacket, (byte) (int) (location.getPitch() * 256.0F / 360.0F));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PacketPlayOutPlayerInfo addInfoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a);
        try {
            Field b = addInfoPacket.getClass().getDeclaredField("b");
            b.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PacketPlayOutPlayerInfo.PlayerInfoData> data = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) b.get(addInfoPacket);
            data.add(new PacketPlayOutPlayerInfo.PlayerInfoData(profile, -1, EnumGamemode.a, new ChatMessage("[DB]")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (VPlayer p : player.getMatch().getPlayers().values()) {
            PlayerConnection connection = ((CraftPlayer) p.getPlayer()).getHandle().b;
            connection.sendPacket(addInfoPacket);
            connection.sendPacket(spawnPacket);
            connection.sendPacket(movePacket);
            makePlayerSleep(connection, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
    }

    public void despawn() {
        PacketPlayOutPlayerInfo removeInfoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e);
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy();
        try {
            Field b = removeInfoPacket.getClass().getDeclaredField("b");
            b.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PacketPlayOutPlayerInfo.PlayerInfoData> data = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) b.get(removeInfoPacket);
            data.add(new PacketPlayOutPlayerInfo.PlayerInfoData(profile, -1, EnumGamemode.b, new ChatMessage("[DB]")));
            for (VPlayer p : player.getMatch().getPlayers().values()) {
                PlayerConnection connection = ((CraftPlayer) p.getPlayer()).getHandle().b;
                connection.sendPacket(removeInfoPacket);
                connection.sendPacket(destroyPacket);
            }
            DEAD_BODIES.remove(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VPlayer getBelongingPlayer() {
        return player;
    }

    public void revive() {
        player.setDead(false);
        player.getPlayer().spigot().respawn();
        player.getPlayer().setSpectatorTarget(null);
        player.getPlayer().teleport(location);
        despawn();
    }

    public static void clear(Match match) {
        Collection<DeadBody> bodies = DEAD_BODIES.values();
        for (DeadBody deadBody : bodies) {
            if (deadBody.player.getMatch() == match) {
                deadBody.despawn();
            }
        }
    }
}
