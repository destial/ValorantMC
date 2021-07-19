package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.Ultimate;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BladeStorm extends Ultimate {
    private ScheduledTask task;
    public BladeStorm() {
        maxUses = -1;
        hold = true;
        task = null;
    }
    @Override
    public void use(Player player, Vector direction) {
        Vector target = player.getLocation().getDirection().clone();
        target.setY(0);
        target.normalize();

        Vector right = new Vector(0, 1, 0).crossProduct(target).normalize();

        final Set<ArmorStand> asList = new HashSet<>();
        ArmorStand as1 = Effects.getBladeStormArmorStand(player.getLocation());
        as1.setRightArmPose(Position.LOWER_RIGHT.ANGLE);
        as1.teleport(player.getEyeLocation().add(right).subtract(0, 0.1, 0));
        asList.add(as1);

        ArmorStand as2 = Effects.getBladeStormArmorStand(player.getLocation());
        as2.setRightArmPose(Position.UPPER_RIGHT.ANGLE);
        as2.teleport(player.getEyeLocation().add(right).add(0, 0.1, 0));
        asList.add(as2);

        ArmorStand as3 = Effects.getBladeStormArmorStand(player.getLocation());
        as3.setRightArmPose(Position.MIDDLE.ANGLE);
        as3.teleport(player.getEyeLocation().add(0, 0.1, 0));
        asList.add(as3);

        ArmorStand as4 = Effects.getBladeStormArmorStand(player.getLocation());
        as4.setRightArmPose(Position.LOWER_LEFT.ANGLE);
        as4.teleport(player.getEyeLocation().add(new Vector(-right.getX(), -0.1, -right.getZ())));
        asList.add(as4);

        ArmorStand as5 = Effects.getBladeStormArmorStand(player.getLocation());
        as5.setRightArmPose(Position.UPPER_LEFT.ANGLE);
        as5.teleport(player.getEyeLocation().add(new Vector(-right.getX(), 0.1, -right.getZ())));
        asList.add(as5);

        task = Scheduler.repeat(() -> {
            for (ArmorStand a : asList) {
                Vector dist = player.getEyeLocation().subtract(a.getLocation()).toVector();
                a.teleport(a.getLocation().add(dist).setDirection(player.getLocation().getDirection()));
            }
        }, 1L);
        Scheduler.delay(() -> {
            task.cancel();
            for (final ArmorStand a : asList) {
                a.remove();
            }
            asList.clear();
        }, 20 * 10L);
    }

    @Override
    public String getName() {
        return "Blade Storm";
    }

    @Override
    public void remove() {

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
            this.ANGLE = new EulerAngle(Math.PI, -Math.PI * 2, 0);
        }
    }
}
