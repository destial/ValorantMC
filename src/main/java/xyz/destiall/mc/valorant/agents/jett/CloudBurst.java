package xyz.destiall.mc.valorant.agents.jett;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Smoke;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.time.Duration;

public class CloudBurst extends Ability implements Smoke {
    private final Vector gravity = new Vector(0, -1F, 0);
    private final AtomicDouble time = new AtomicDouble(0D);
    private ScheduledTask smokeTravelTask;
    private Location l;

    public CloudBurst() {
        maxUses = 3;
        agent = Agent.JETT;
        smokeTravelTask = null;
        hold = false;
    }

    @Override
    public void use(Player player, Vector direction) {
        l = player.getEyeLocation().clone();
        smokeTravelTask = Scheduler.repeat(() -> {
            l.add(direction).add(gravity.multiply(time.get()));
            time.addAndGet(0.1D);
            Effects.smokeTravel(l, agent);
            if (!l.getBlock().isPassable()) {
                this.appear(l);
            }
        }, 1L);
    }

    @Override
    public String getName() {
        return "CloudBurst";
    }

    @Override
    public void remove() {
        this.dissipate();
    }

    @Override
    public ItemStack getShopDisplay() {
        return null;
    }

    @Override
    public Integer getPrice() {
        return 400;
    }

    @Override
    public void appear(Location location) {
        if (smokeTravelTask != null) {
            Scheduler.cancel(smokeTravelTask);
        }
        Effects.smoke(location, agent, getSmokeDuration().toMillis() / 1000D);
    }

    @Override
    public void dissipate() {

    }

    @Override
    public Duration getSmokeDuration() {
        return Duration.ofSeconds(4).plus(Duration.ofMillis(500));
    }

    @Override
    public double getSmokeRange() {
        return 4;
    }
}
