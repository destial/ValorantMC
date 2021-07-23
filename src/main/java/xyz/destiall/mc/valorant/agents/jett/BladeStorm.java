package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.abilities.Ultimate;
import xyz.destiall.mc.valorant.managers.AbilityManager;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BladeStorm extends Ultimate implements Listener {
    private ScheduledTask task;
    private ScheduledTask removalTask;
    private final HashMap<Player, HashMap<ArmorStand, ScheduledTask>> using = new HashMap<>();
    private final List<ArmorStand> asList = new ArrayList<>();
    public BladeStorm() {
        maxUses = -1;
        hold = true;
        task = null;
    }
    @Override
    public void use(Player player, Vector direction) {
        Vector target = player.getLocation().clone().getDirection().clone();
        target.setY(0);
        target.normalize();

        Vector right = new Vector(0, 1, 0).crossProduct(target).normalize();
        Location eyeLocation = player.getEyeLocation().clone();

        ArmorStand as1 = Effects.getBladeStormArmorStand(eyeLocation.clone());
        as1.setInvisible(false);
        as1.setRightArmPose(Position.LOWER_RIGHT.ANGLE);
        as1.teleport(eyeLocation.clone().add(right).subtract(0, 1, 0));
        asList.add(as1);

        ArmorStand as2 = Effects.getBladeStormArmorStand(eyeLocation.clone());
        as2.setRightArmPose(Position.UPPER_RIGHT.ANGLE);
        as2.teleport(eyeLocation.clone().add(right).add(0, 1, 0));
        asList.add(as2);

        ArmorStand as3 = Effects.getBladeStormArmorStand(eyeLocation.clone());
        as3.setRightArmPose(Position.MIDDLE.ANGLE);
        as3.teleport(eyeLocation.clone().add(0, 1, 0));
        asList.add(as3);

        ArmorStand as4 = Effects.getBladeStormArmorStand(eyeLocation.clone());
        as4.setRightArmPose(Position.LOWER_LEFT.ANGLE);
        as4.teleport(eyeLocation.clone().add(new Vector(-right.getX(), -1, -right.getZ())));
        asList.add(as4);

        ArmorStand as5 = Effects.getBladeStormArmorStand(eyeLocation.clone());
        as5.setRightArmPose(Position.UPPER_LEFT.ANGLE);
        as5.teleport(eyeLocation.clone().add(new Vector(-right.getX(), 1, -right.getZ())));
        asList.add(as5);
        Bukkit.getPluginManager().registerEvents(this, Valorant.getInstance().getPlugin());

        using.put(player, new HashMap<>());

        task = Scheduler.repeat(() -> {
            for (ArmorStand a : asList) {
                Location eye = player.getEyeLocation().clone();
                Vector dist = eye.clone().subtract(a.getLocation().clone()).toVector();
                Vector d = eye.getDirection().clone();
                a.teleport(a.getLocation().clone().add(dist.clone()).setDirection(d));
            }
        }, 1L);
        removalTask = Scheduler.delay(() -> {
            task.cancel();
            for (final ArmorStand a : asList) {
                a.remove();
            }
            for (ScheduledTask t : using.get(player).values()) {
                t.cancel();
            }
            using.get(player).clear();
            asList.clear();
            using.remove(player);
            HandlerList.unregisterAll(this);
        }, 20 * 10L);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (using.get(e.getPlayer()) == null) return;
        if (e.getAction() == Action.LEFT_CLICK_AIR) {
            int i = 0;
            ArmorStand as = asList.get(i);
            while (using.get(e.getPlayer()).get(as) != null && i < asList.size()) {
                as = asList.get(i);
                i++;
            }
            if (as == null) return;
            Vector direction = e.getPlayer().getLocation().getDirection().clone();
            ArmorStand finalAs = as;
            int finalI = i;
            ScheduledTask t = Scheduler.repeat(() -> {
                finalAs.teleport(finalAs.getLocation().add(direction.multiply(0.5)));
                e.getPlayer().sendMessage("travelling " + finalI);
                List<Entity> entities = finalAs.getLocation().getWorld().getNearbyEntities(finalAs.getLocation(), 1, 1, 1).stream().filter(en -> en instanceof LivingEntity).collect(Collectors.toList());
                if (entities.size() == 0) return;
                for (Entity en : entities) {
                    if (en instanceof ArmorStand) continue;
                    if (en == e.getPlayer()) continue;
                    e.getPlayer().sendMessage("hit entity for as " + finalI);
                    LivingEntity live = (LivingEntity) en;
                    if (!live.getBoundingBox().contains(finalAs.getBoundingBox())) continue;
                    live.damage(20);
                    finalAs.remove();
                    asList.remove(finalAs);
                    using.get(e.getPlayer()).get(finalAs).cancel();
                }
            }, 1L);
            using.get(e.getPlayer()).put(finalAs, t);
        }
    }

    @Override
    public String getName() {
        return "Blade Storm";
    }

    @Override
    public void remove() {
        HandlerList.unregisterAll(this);
        if (task != null) {
            task.cancel();
        }
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

    enum Position {
        UPPER_LEFT,
        LOWER_LEFT,
        MIDDLE,
        LOWER_RIGHT,
        UPPER_RIGHT;

        public final EulerAngle ANGLE;
        Position() {
            this.ANGLE = new EulerAngle(0, 0, 0);
        }
    }
}
