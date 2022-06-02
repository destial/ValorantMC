package xyz.destiall.mc.valorant.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Versioning {
    public static ServerLevel getWorld(org.bukkit.World world) {
        try {
            return ((CraftWorld) world).getHandle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("all")
    public static ServerLevel getWorldServer(org.bukkit.World world) {
        return getWorld(world).getMinecraftWorld();
    }

    @SuppressWarnings("all")
    public static MinecraftServer getMinecraftServer(org.bukkit.World world) {
        return getWorld(world).getServer();
    }

    public static ServerPlayer getPlayer(Player player) {
        try {
            return ((CraftPlayer) player).getHandle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getConnection(Player player) {
        return getPlayer(player).connection.getConnection();
    }

    public static GameProfile getGameProfile(Player player) {
        try {
            return getPlayer(player).getGameProfile();
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

    public static ClientboundRemoveEntitiesPacket newDestroyPacket(int entityId) {
            return new ClientboundRemoveEntitiesPacket(entityId);
    }

    public static ItemStack getItemStack(org.bukkit.inventory.ItemStack bukkit) {
        try {
            return CraftItemStack.asNMSCopy(bukkit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
