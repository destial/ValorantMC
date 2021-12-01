package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ultimate;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.Pair;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BladeStorm extends Ultimate implements Listener {
    private ScheduledTask removalTask;
    private final HashMap<Pair<ArmorStand, Vector>, ScheduledTask> using = new HashMap<>();
    private final HashMap<ArmorStand, ScheduledTask> knives = new HashMap<>();
    private final ItemStack dSword;

    public BladeStorm(VPlayer player) {
        super(player);
        maxUses = -1;
        hold = true;
        dSword = new ItemStack(Material.DIAMOND_SWORD);
    }
    @Override
    public void use() {
        if (using.size() != 0) return;
        for (double i = -Math.PI; i <= Math.PI; i += Math.PI / 4) {
            double x = Math.cos(i);
            double z = Math.sin(i);
            final ArmorStand as = Effects.getBladeStormArmorStand(player.getLocation());
            as.setItemInHand(dSword.clone());
            Pair<ArmorStand, Vector> asIpair = new Pair<>(as, new Vector(x, 0, z));
            using.put(asIpair, Scheduler.repeat(() -> {
                Location ll = player.getLocation().add(0, player.getPlayer().getEyeHeight() * 0.5, 0);
                Vector dr = ll.getDirection();
                dr = dr.setY(0).normalize();
                ll.add(dr.clone().multiply(0.3));
                Vector right = dr.crossProduct(new Vector(0, 1, 0)).normalize();
                ll.subtract(right.multiply(0.2));
                Vector asVect = asIpair.getValue();
                if (using.size() != 0) {
                    as.teleport(ll.add(asVect));
                } else {
                    as.remove();
                }
            }, 1L));
        }
        removalTask = Scheduler.delay(using::clear, 20 * 60L);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent ev) {
        if (using.size() == 0) return;
        if (ev.getAction() == Action.LEFT_CLICK_AIR) {
            ev.setUseItemInHand(Event.Result.DENY);
            ev.getPlayer().getInventory().setItemInHand(null);
            final Location origin = player.getEyeLocation().clone();
            Scheduler.run(() -> {
                final Location loc = player.getLocation();
                final Vector dir = player.getLocation().getDirection().multiply(2);
                final ArmorStand as = Effects.getBladeStormArmorStand(origin);
                as.setItemInHand(dSword);
                knives.put(as, Scheduler.repeat(() -> {
                    loc.add(dir);
                    if (!loc.getBlock().isPassable()) {
                        knives.remove(as).cancel();
                        as.remove();
                        return;
                    }
                    as.teleport(loc);
                    List<Entity> hit = as.getNearbyEntities(0.5, 0.5, 0.5).stream().filter(e -> e instanceof LivingEntity && e != as && !e.getUniqueId().equals(player.getUUID())).filter(e -> {
                        LivingEntity le = (LivingEntity) e;
                        if (le instanceof Player) {
                            VPlayer p = player.getMatch().getPlayers().get(le.getUniqueId());
                            if (p == null) return false;
                            return !p.getTeam().getSide().equals(player.getTeam().getSide());
                        }
                        return false;
                    }).collect(Collectors.toList());
                    for (Entity e : hit) {
                        LivingEntity live = (LivingEntity) e;
                        live.damage(5);
                    }
                    if (loc.distance(origin) >= 20) {
                        knives.remove(as).cancel();
                        as.remove();
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
        if (removalTask != null) {
            removalTask.run();
            removalTask.cancel();
        }
    }

    @Override
    public ItemStack getShopDisplay() {
        return null;
    }

    @Override
    public Integer getPrice() {
        return null;
    }
}
