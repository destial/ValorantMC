package xyz.destiall.mc.valorant.agents.jett;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Smoke;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.time.Duration;

public class CloudBurst extends Ability implements Smoke {
    private final Vector gravity = new Vector(0, -1f, 0);
    private final AtomicDouble time = new AtomicDouble(0d);
    private ScheduledTask smokeTravelTask;
    private ScheduledTask smokeTask;
    private Location l;

    public CloudBurst(VPlayer player) {
        super(player);
        maxUses = 3;
        agent = Agent.JETT;
        smokeTravelTask = null;
        trigger = Trigger.RIGHT;
        cancel = true;
        item = new ItemStack(Material.SNOWBALL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + getName());
        item.setItemMeta(meta);
    }

    @Override
    public void use() {
        l = player.getEyeLocation();
        smokeTravelTask = Scheduler.repeat(() -> {
            Vector direction = player.getDirection();
            l.add(direction.multiply(2)).add(gravity.multiply(time.get()));
            time.addAndGet(0.15);
            Effects.smokeTravel(l, agent);
            if (!l.getBlock().isPassable()) {
                this.appear(l);
            }
        }, 1L);
        ItemStack snowball = player.getPlayer().getInventory().getItemInMainHand();
        snowball.setAmount(snowball.getAmount() - 1);
    }

    @Override
    public String getName() {
        return "Cloud Burst";
    }

    @Override
    public void remove() {
        this.dissipate();
    }

    @Override
    public ItemStack getShopDisplay() {
        return item.clone();
    }

    @Override
    public Integer getPrice() {
        return 400;
    }

    @Override
    public void appear(Location location) {
        if (smokeTravelTask != null) {
            smokeTravelTask.cancel();
        }
        smokeTask = Effects.smoke(player.getMatch(), location, agent, getSmokeDuration().toMillis() / 1000D);
    }

    @Override
    public void dissipate() {
        if (smokeTask != null && !smokeTask.getTask().isCancelled()) {
            smokeTask.cancel();
        }
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
