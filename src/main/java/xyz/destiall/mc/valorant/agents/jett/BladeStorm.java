package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.Ultimate;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.HashSet;
import java.util.Set;

public class BladeStorm extends Ultimate {
    private ScheduledTask task;
    private final Set<ArmorStand> asList = new HashSet<>();
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
        Location eyeLocation = player.getLocation().clone();
        eyeLocation.add(0, 2, 0);

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

        task = Scheduler.repeat(() -> {
            for (ArmorStand a : asList) {
                Location eye = player.getLocation().clone();
                eye.add(0, 2, 0);
                Vector dist = eye.clone().subtract(a.getLocation().clone()).toVector();
                Vector d = eye.getDirection().clone();
                a.teleport(a.getLocation().clone().add(dist).setDirection(d));
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
            this.ANGLE = new EulerAngle(0, 0, 0);
        }
    }
}
