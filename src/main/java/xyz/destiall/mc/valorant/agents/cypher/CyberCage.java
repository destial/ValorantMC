package xyz.destiall.mc.valorant.agents.cypher;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Smoke;
import xyz.destiall.mc.valorant.utils.Effects;

import java.time.Duration;

public class CyberCage extends Ability implements Smoke {
    private int smokeTravelTask;
    public CyberCage() {
        smokeTravelTask = -1;
        agent = Agent.CYPHER;
    }
    @Override
    public void use(Player player, Vector direction) {
        this.appear(player.getLocation());
    }

    @Override
    public String getName() {
        return null;
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

    @Override
    public void appear(Location location) {
        if (smokeTravelTask != -1) {
            Bukkit.getScheduler().cancelTask(smokeTravelTask);
        }
        Effects.smoke(location, agent, getSmokeDuration().getSeconds());
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
}
