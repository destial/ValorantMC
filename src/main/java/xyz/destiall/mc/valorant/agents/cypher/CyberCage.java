package xyz.destiall.mc.valorant.agents.cypher;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Smoke;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.time.Duration;

public class CyberCage extends Ability implements Smoke, Listener {
    private final Team team;
    private Location finalLoc;
    private ScheduledTask cageTask;
    public CyberCage(VPlayer player) {
        super(player);
        agent = Agent.CYPHER;
        cageTask = null;
        finalLoc = null;
        team = player.getTeam();
        trigger = Trigger.RIGHT;
    }

    @Override
    public void use() {
        appear(player.getLocation().subtract(new Vector(0, -2, 0)));
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void remove() {
        if (cageTask == null) return;
        Scheduler.cancel(cageTask);
        dissipate();
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
        finalLoc = location;
        Bukkit.getPluginManager().registerEvents(this, Valorant.getInstance().getPlugin());
        cageTask = Effects.smoke(player.getMatch(), location, agent, getSmokeDuration().getSeconds());
        Scheduler.delay(this::dissipate, getSmokeDuration().toMillis() / 1000L * 20L);
    }

    @Override
    public void dissipate() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onWalkThrough(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        if (e.getTo().toVector().isInSphere(finalLoc.toVector(), getSmokeRange())) {
            if (team != null) {
                VPlayer p = MatchManager.getInstance().getPlayer(e.getPlayer());
                if (p == null) return;
                if (p.getTeam() == team) return;
                p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 1));
                return;
            }
            Player player = e.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 1));
        }
    }

    @Override
    public Duration getSmokeDuration() {
        return Duration.ofSeconds(7);
    }

    @Override
    public double getSmokeRange() {
        return 2.1;
    }
}
