package xyz.destiall.mc.valorant.agents.jett;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Smoke;
import xyz.destiall.mc.valorant.utils.Effects;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CloudBurst extends Ability implements Smoke {
    public static final HashMap<UUID, CloudBurst> CLOUDBURST_DATA = new HashMap<>();
    private Duration smokeDuration;
    private long timer;
    private int smokeTravelTask;
    public CloudBurst() {
        uses = 0;
        maxUses = 3;
        agent = Agent.JETT;
        smokeDuration = null;
        smokeTravelTask = -1;
        hold = false;
    }

    @Override
    public void use(Player player, Vector direction) {
        if (uses >= maxUses) return;
        CLOUDBURST_DATA.put(player.getUniqueId(), this);
        final Location l = player.getEyeLocation().clone();
        final Vector gravity = new Vector(0, -1F, 0);
        final AtomicDouble time = new AtomicDouble(0D);
        smokeTravelTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Valorant.getInstance().getPlugin(), () -> {
            l.add(direction).add(gravity.multiply(time.get()));
            time.addAndGet(0.1D);
            Effects.smokeTravel(l, agent);
            if (!l.getBlock().isPassable()) {
                this.appear(l);
            }
        }, 0L, 1L);
        uses++;
    }

    @Override
    public String getName() {
        return "CloudBurst";
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
    public void update() {
        if (smokeDuration == null) return;
        smokeDuration = smokeDuration.minus(Duration.of(System.currentTimeMillis() - timer, ChronoUnit.MILLIS));
        timer = System.currentTimeMillis();
    }

    @Override
    public void appear(Location location) {
        smokeDuration = getSmokeDuration();
        timer = System.currentTimeMillis();
        if (smokeTravelTask != -1) {
            Bukkit.getScheduler().cancelTask(smokeTravelTask);
        }
        Effects.smoke(location, agent, getSmokeDuration().getSeconds());
    }

    public static void updateSmoke() {
        if (CLOUDBURST_DATA.size() == 0) return;
        final List<UUID> remove = new ArrayList<>();
        for (Map.Entry<UUID, CloudBurst> cloudBurst : CLOUDBURST_DATA.entrySet()) {
            cloudBurst.getValue().update();
            Duration smoke = cloudBurst.getValue().getSmokeLastingDuration();
            if (smoke == null) continue;
            if (smoke.isNegative() || smoke.isZero()) {
                remove.add(cloudBurst.getKey());
            }
        }
        for (UUID id : remove) {
            CLOUDBURST_DATA.remove(id);
        }
    }

    @Override
    public void dissipate() {

    }

    @Override
    public Duration getSmokeDuration() {
        return Duration.of(4, ChronoUnit.SECONDS).plus(Duration.of(500, ChronoUnit.MILLIS));
    }

    @Override
    public int getSmokeRange() {
        return 4;
    }

    @Override
    public Duration getSmokeLastingDuration() {
        return smokeDuration;
    }
}
