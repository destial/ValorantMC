package xyz.destiall.mc.valorant.agents.jett;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.abilities.Ultimate;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;
import xyz.destiall.mc.valorant.utils.Versioning;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BladeStorm extends Ultimate implements Listener {
    private final Map<ArmorStand, Vector> blades = new ConcurrentHashMap<>();
    private final Map<ArmorStand, ScheduledTask> knives = new ConcurrentHashMap<>();
    private int uses;

    public BladeStorm(VPlayer player) {
        super(player);
        maxUses = 4;
        trigger = Trigger.HOLD;
        item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + getName());
        item.setItemMeta(meta);
    }

    @Override
    public void use() {
        if (blades.size() != 0) return;
        Bukkit.getPluginManager().registerEvents(this, Valorant.getInstance().getPlugin());
        spawnBlades();
        uses = 0;
        using = true;
    }

    private void spawnBlades() {
        Location loc = player.getLocation().add(0, player.getPlayer().getEyeHeight() * 0.5, 0);
        Vector dir = loc.getDirection().setY(0).normalize();
        loc.subtract(dir.clone().multiply(0.3));
        Vector right = dir.crossProduct(new Vector(0, 1, 0)).normalize();
        loc.subtract(right.multiply(0.2));
        for (double i = 0; i <= Math.PI; i += Math.PI / 4) {
            double x = Math.cos(i);
            double z = Math.sin(i);
            final ArmorStand as = Effects.getBladeStormArmorStand(loc);
            Effects.sendArmorStand(as, player.getMatch(), item);
            double yaw = Math.toRadians(-loc.getYaw());
            double xRotate = Math.cos(yaw) * x + Math.sin(yaw) * z;
            double zRotate = -Math.sin(yaw) * x + Math.cos(yaw) * z;
            Effects.teleportArmorStand(loc.add(xRotate, 0, zRotate), as, player.getMatch());
            blades.put(as, new Vector(x, 0, z));
            loc.subtract(xRotate, 0, zRotate);
        }
    }

    private void despawnBlades() {
        for (Map.Entry<ArmorStand, Vector> entry : blades.entrySet()) {
            Effects.removeArmorStand(entry.getKey(), player.getMatch());
        }
        blades.clear();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.getPlayer().getUniqueId().equals(player.getUUID())) return;
        Location loc = player.getLocation().add(0, player.getPlayer().getEyeHeight() * 0.5, 0);
        Vector dir = loc.getDirection().setY(0).normalize();
        loc.subtract(dir.clone().multiply(0.3));
        Vector right = dir.crossProduct(new Vector(0, 1, 0)).normalize();
        loc.subtract(right.multiply(0.2));
        for (Map.Entry<ArmorStand, Vector> entry : blades.entrySet()) {
            Vector vector = entry.getValue();
            double yaw = Math.toRadians(-loc.getYaw());
            double xRotate = Math.cos(yaw) * vector.getX() + Math.sin(yaw) * vector.getZ();
            double zRotate = -Math.sin(yaw) * vector.getX() + Math.cos(yaw) * vector.getZ();
            Effects.teleportArmorStand(loc.add(xRotate, 0, zRotate), entry.getKey(), player.getMatch());
            loc.subtract(xRotate, 0, zRotate);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHotbar(PlayerItemHeldEvent e) {
        if (!e.getPlayer().getUniqueId().equals(player.getUUID())) return;
        int slot = e.getNewSlot();
        int abilitySlot = player.getAbilities().entrySet().stream().filter(en -> en.getValue() == this).findFirst().orElseThrow().getKey();
        if (slot != abilitySlot) {
            despawnBlades();
        } else {
            spawnBlades();
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent ev) {
        if (!ev.getPlayer().getUniqueId().equals(player.getUUID()) || blades.size() == 0 || uses >= maxUses) return;
        if (ev.getAction() == Action.LEFT_CLICK_AIR) {
            ev.setUseItemInHand(Event.Result.DENY);
            final Location origin = player.getEyeLocation();
            uses++;
            if (uses >= maxUses) {
                player.setUseUlt(false);
                remove();
            }
            final Location loc = player.getEyeLocation();
            final Vector dir = loc.getDirection().multiply(2);
            final ArmorStand as = Effects.getBladeStormArmorStand(loc);
            Effects.sendArmorStand(as, player.getMatch(), item);
            Effects.teleportArmorStand(loc, as, player.getMatch());
            ArmorStand blade = blades.keySet().stream().findAny().orElse(null);
            if (blade == null) {
                return;
            }
            Effects.removeArmorStand(blade, player.getMatch());
            blades.remove(blade);
            knives.put(as, Scheduler.repeat(() -> {
                loc.add(dir);
                if (!loc.getBlock().isPassable()) {
                    knives.remove(as).cancel();
                    Effects.removeArmorStand(as, player.getMatch());
                    return;
                }
                Effects.teleportArmorStand(loc, as, player.getMatch());
                List<Entity> hit = Versioning.getWorld(loc.getWorld()).getEntities(as, as.getBoundingBox().inflate(0.1, 0.1, 0.1), entity -> true).stream().filter(e -> e instanceof net.minecraft.world.entity.LivingEntity && e != as && !e.getUUID().equals(player.getUUID())).filter(e -> {
                    net.minecraft.world.entity.LivingEntity le = (net.minecraft.world.entity.LivingEntity) e;
                    if (le instanceof ServerPlayer) {
                        VPlayer p = player.getMatch().getPlayer(le.getUUID());
                        if (p == null) return false;
                        return !p.getTeam().getSide().equals(player.getTeam().getSide());
                    } else {
                        return true;
                    }
                }).toList();
                for (Entity e : hit) {
                    net.minecraft.world.entity.LivingEntity live = (net.minecraft.world.entity.LivingEntity) e;
                    ((LivingEntity) live.getBukkitEntity()).damage(5);
                }
                if (hit.size() > 0 || loc.distanceSquared(origin) >= 20 * 20) {
                    knives.remove(as).cancel();
                    Effects.removeArmorStand(as, player.getMatch());
                }
            }, 1L));
        }
    }

    @Override
    public String getName() {
        return "Blade Storm";
    }

    @Override
    public void remove() {
        despawnBlades();
        HandlerList.unregisterAll(this);
        for (Map.Entry<ArmorStand, ScheduledTask> entry : knives.entrySet()) {
            Effects.removeArmorStand(entry.getKey(), player.getMatch());
            entry.getValue().cancel();
        }
        knives.clear();
        using = false;
    }

    @Override
    public Integer getPrice() {
        return null;
    }
}
