package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Smoke;
import xyz.destiall.mc.valorant.utils.Effects;

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
    private int smokeTravelTask;
    private int smokeTask;
    private final Match match;
    public CloudBurst(Match match) {
        maxUses = 3;
        agent = Agent.JETT;
        smokeDuration = null;
        smokeTravelTask = -1;
        smokeTask = -1;
        this.match = match;
    }

    @Override
    public void use(Participant participant, Vector direction) {
        if (uses >= maxUses) return;
        CLOUDBURST_DATA.put(participant.getUUID(), this);
        final List<Location> loc = new ArrayList<>();
        loc.add(participant.getPlayer().getEyeLocation());
        smokeTravelTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Valorant.getInstance().getPlugin(), () -> {
            Location l = loc.get(0);
            loc.clear();
            Location nL = l.add(direction).add(new Vector(0, -9.8F * 1 / 20, 0));
            loc.add(nL);
            if (nL.getBlock().isEmpty()) {
                this.appear(nL);
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
            smokeTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Valorant.getInstance().getPlugin(), () -> {
                Effects.smoke(location, Effects.Type.JETT, match);
            }, 0L, 1L);
        }
    }

    public static void updateSmoke() {
        final List<UUID> remove = new ArrayList<>();
        for (Map.Entry<UUID, CloudBurst> cloudBurst : CLOUDBURST_DATA.entrySet()) {
            cloudBurst.getValue().update();
            Duration smoke = cloudBurst.getValue().getSmokeLastingDuration();
            if (smoke == null) continue;
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
        if (smokeTask == -1) return;
        Bukkit.getScheduler().cancelTask(smokeTask);
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
