package xyz.destiall.mc.valorant.api.deadbodies;

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
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.Location;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Scheduler;
import xyz.destiall.mc.valorant.utils.Versioning;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class DeadBody {
    private int entityId;
    private GameProfile profile;
    private final Location location;
    private final VPlayer player;

    public DeadBody(VPlayer player) {
        this.player = player;
        this.location = player.getLocation().clone();
    }

    public void spawn() {
        entityId = Versioning.getNextEntityIdAtomic().get();
        profile = Versioning.cloneProfileWithRandomUUID(Versioning.getGameProfile(player.getPlayer()), "");
        DataWatcher dw = Versioning.clonePlayerDatawatcher(player.getPlayer(), entityId);
        DataWatcherObject<Byte> skinFlags = new DataWatcherObject<>(16, DataWatcherRegistry.a);
        dw.set(skinFlags, (byte)0x7F);
        Location locUnder = Versioning.getNonClippableBlockUnderPlayer(location, 1);
        Location used = locUnder != null ? locUnder : location;
        player.setDead(true);
        spawn(used);
    }

    private void makePlayerSleep(PlayerConnection conn, BlockPosition bedPos) {
        EntityPlayer entityPlayer = new EntityPlayer(Versioning.getMinecraftServer(player.getPlayer().getWorld()), Versioning.getWorldServer(player.getPlayer().getWorld()), profile);
        entityPlayer.e(entityId);
        try {
            Field poseF = Entity.class.getDeclaredField("ad");
            poseF.setAccessible(true);
            @SuppressWarnings("unchecked")
            DataWatcherObject<EntityPose> POSE = (DataWatcherObject<EntityPose>) poseF.get(null);
            entityPlayer.getDataWatcher().set(POSE, EntityPose.c);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        entityPlayer.entitySleep(bedPos);
        conn.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), entityPlayer.getDataWatcher(), false));
    }

    private void spawn(Location location) {
        System.out.println("Spawning dead body");
        PacketPlayOutEntity.PacketPlayOutRelEntityMove movePacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(entityId, (short) (0), (short) (-61.8), (short) (0), false);
        @SuppressWarnings("all")
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(Versioning.getPlayer(player.getPlayer()));
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
            data.add(new PacketPlayOutPlayerInfo.PlayerInfoData(profile, -1, EnumGamemode.a, new ChatMessage("")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PacketPlayOutPlayerInfo removeInfoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e);
        Collection<VPlayer> list = player.getMatch().getPlayers().values();
        for (VPlayer p : list) {
            PlayerConnection connection = Versioning.getConnection(p.getPlayer());
            connection.sendPacket(addInfoPacket);
            Scheduler.delay(() -> connection.sendPacket(removeInfoPacket), 3L);
            connection.sendPacket(spawnPacket);
            connection.sendPacket(movePacket);
            makePlayerSleep(connection, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
    }

    public void despawn() {
        System.out.println("Despawning dead body");
        PacketPlayOutPlayerInfo removeInfoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e);
        PacketPlayOutEntityDestroy destroyPacket = Versioning.newDestroyPacket(entityId);
        try {
            Field b = removeInfoPacket.getClass().getDeclaredField("b");
            b.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PacketPlayOutPlayerInfo.PlayerInfoData> data = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) b.get(removeInfoPacket);
            data.add(new PacketPlayOutPlayerInfo.PlayerInfoData(profile, -1, EnumGamemode.b, new ChatMessage("")));
            Collection<VPlayer> list = player.getMatch().getPlayers().values();
            for (VPlayer p : list) {
                PlayerConnection connection = Versioning.getConnection(p.getPlayer());
                connection.sendPacket(removeInfoPacket);
                connection.sendPacket(destroyPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void revive() {
        player.setDead(false);
        player.getPlayer().spigot().respawn();
        player.getPlayer().setSpectatorTarget(null);
        player.getPlayer().teleport(location);
        despawn();
    }
}
