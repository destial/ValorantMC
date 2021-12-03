package xyz.destiall.mc.valorant.agents.jett;

import com.google.common.base.Predicates;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.abilities.Ultimate;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.Pair;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;
import xyz.destiall.mc.valorant.utils.Versioning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BladeStorm extends Ultimate implements Listener {
    private final HashMap<Pair<EntityArmorStand, Vector>, ScheduledTask> using = new HashMap<>();
    private final HashMap<EntityArmorStand, ScheduledTask> knives = new HashMap<>();
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
        if (using.size() != 0) return;
        Bukkit.getPluginManager().registerEvents(this, Valorant.getInstance().getPlugin());
        spawnBlades();
        uses = 0;
    }

    private void spawnBlades() {
        for (double i = -Math.PI; i <= Math.PI; i += Math.PI / 4) {
            double x = Math.cos(i);
            double z = Math.sin(i);
            final EntityArmorStand as = Effects.getBladeStormArmorStand(player.getLocation());
            Effects.sendArmorStand(as, player.getMatch());
            as.setSlot(EnumItemSlot.a,  CraftItemStack.asNMSCopy(item.clone()));
            Pair<EntityArmorStand, Vector> asIpair = new Pair<>(as, new Vector(x, 0, z));
            using.put(asIpair, Scheduler.repeat(() -> {
                Location ll = player.getLocation().add(0, player.getPlayer().getEyeHeight() * 0.5, 0);
                Vector dr = ll.getDirection();
                dr = dr.setY(0).normalize();
                ll.add(dr.clone().multiply(0.3));
                Vector right = dr.crossProduct(new Vector(0, 1, 0)).normalize();
                ll.subtract(right.multiply(0.2));
                Vector asVect = asIpair.getValue();
                if (using.size() != 0) {
                    Effects.teleportArmorStand(ll.add(asVect), as, player.getMatch());
                } else {
                    Effects.removeArmorStand(as, player.getMatch());
                }
            }, 1L));
        }
    }

    private void despawnBlades() {
        Scheduler.run(() -> {
            for (Map.Entry<Pair<EntityArmorStand, Vector>, ScheduledTask> entry : using.entrySet()) {
                entry.getValue().cancel();
                Effects.removeArmorStand(entry.getKey().getKey(), player.getMatch());
            }
            using.clear();
        });
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

    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEvent ev) {
        if (!ev.getPlayer().getUniqueId().equals(player.getUUID()) || using.size() == 0 || uses >= maxUses) return;
        if (ev.getAction() == Action.LEFT_CLICK_AIR) {
            ev.setUseItemInHand(Event.Result.DENY);
            //ev.getPlayer().getInventory().setItemInHand(null);
            final Location origin = player.getEyeLocation().clone();
            uses++;
            if (uses >= maxUses) {
                player.setUseUlt(false);
                remove();
            }
            Scheduler.run(() -> {
                final Location loc = player.getLocation();
                final Vector dir = player.getLocation().getDirection().multiply(2);
                final EntityArmorStand as = Effects.getBladeStormArmorStand(origin);
                Effects.sendArmorStand(as, player.getMatch());
                as.setSlot(EnumItemSlot.a,  CraftItemStack.asNMSCopy(item.clone()));
                knives.put(as, Scheduler.repeat(() -> {
                    loc.add(dir);
                    if (!loc.getBlock().isPassable()) {
                        knives.remove(as).cancel();
                        Effects.removeArmorStand(as, player.getMatch());
                        return;
                    }
                    Effects.teleportArmorStand(loc, as, player.getMatch());
                    List<Entity> hit = Versioning.getWorld(loc.getWorld()).getEntities(as, as.getBoundingBox().grow(0.5, 0.5, 0.5), Predicates.alwaysTrue()).stream().filter(e -> e instanceof EntityLiving && e != as && !e.getUniqueID().equals(player.getUUID())).filter(e -> {
                        EntityLiving le = (EntityLiving) e;
                        if (le instanceof EntityPlayer) {
                            VPlayer p = player.getMatch().getPlayer(le.getUniqueID());
                            if (p == null) return false;
                            return !p.getTeam().getSide().equals(player.getTeam().getSide());
                        }
                        return false;
                    }).collect(Collectors.toList());
                    for (Entity e : hit) {
                        EntityLiving live = (EntityLiving) e;
                        ((LivingEntity) live.getBukkitEntity()).damage(5);
                    }
                    if (loc.distance(origin) >= 20) {
                        knives.remove(as).cancel();
                        Effects.removeArmorStand(as, player.getMatch());
                    }
                }, 1L));
            });
        }
    }

    @Override
    public String getName() {
        return "Blade Storm";
    }

    @Override
    public void remove() {
        HandlerList.unregisterAll(this);
        despawnBlades();
        Scheduler.delay(() -> {
            for (Map.Entry<EntityArmorStand, ScheduledTask> entry : knives.entrySet()) {
                Effects.removeArmorStand(entry.getKey(), player.getMatch());
                entry.getValue().cancel();
            }
            knives.clear();
        }, 20L);
    }

    @Override
    public ItemStack getShopDisplay() {
        return item.clone();
    }

    @Override
    public Integer getPrice() {
        return null;
    }
}
