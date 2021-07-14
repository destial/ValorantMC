package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Smoke;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CloudBurst extends Ability implements Smoke {
    private static final HashMap<UUID, CloudBurst> CLOUDBURST_DATA = new HashMap<>();
    private Duration smokeDuration;
    private long timer;
    public CloudBurst() {
        maxUses = 3;
        agent = Agent.JETT;
        smokeDuration = null;
    }

    @Override
    public void use(Participant participant, Vector direction) {
        if (uses >= maxUses) return;
        final CloudBurst cloudBurst = new CloudBurst();
        CLOUDBURST_DATA.put(participant.getUUID(), cloudBurst);
        Bukkit.getScheduler().runTaskAsynchronously(Valorant.getInstance().getPlugin(), () -> {
            cloudBurst.appear(participant.getPlayer().getLocation());
        });
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
        smokeDuration = smokeDuration.minus(Duration.of(System.currentTimeMillis() - timer, ChronoUnit.MILLIS));
        timer = System.currentTimeMillis();
    }

    @Override
    public void appear(Location location) {
        smokeDuration = getSmokeDuration();
        timer = System.currentTimeMillis();
    }

    public static void updateSmoke() {
        final List<UUID> remove = new ArrayList<>();
        for (Map.Entry<UUID, CloudBurst> cloudBurst : CLOUDBURST_DATA.entrySet()) {
            cloudBurst.getValue().update();
            Duration smoke = cloudBurst.getValue().getSmokeLastingDuration();
            if (smoke.isNegative() || smoke.isZero()) {
                remove.add(cloudBurst.getKey());
                cloudBurst.getValue().dissipate();
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
