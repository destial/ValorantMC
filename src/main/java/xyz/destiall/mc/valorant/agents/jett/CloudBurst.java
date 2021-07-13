package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.abilities.Smoke;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

public class CloudBurst extends Ability implements Smoke {
    public static final HashMap<UUID, CloudBurst> CLOUDBURST_DATA = new HashMap<>();
    public CloudBurst() {
        maxUses = 3;
    }

    @Override
    public void use(Participant participant, Vector direction) {
        final CloudBurst cloudBurst = new CloudBurst();
        CLOUDBURST_DATA.put(participant.getUUID(), cloudBurst);
        Bukkit.getScheduler().runTaskAsynchronously(Valorant.getInstance().getPlugin(), () -> {
            cloudBurst.appear(participant.getPlayer().getLocation());
        });
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

    }

    @Override
    public void appear(Location location) {

    }

    @Override
    public void dissipate() {

    }

    @Override
    public Duration getSmokeDuration() {
        return null;
    }

    @Override
    public int getSmokeRange() {
        return 0;
    }

    @Override
    public Duration getSmokeLastingDuration() {
        return null;
    }
}
