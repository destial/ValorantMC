package xyz.destiall.mc.valorant.agents.reyna;

import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Flash;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.Collection;

public class Leer extends Ability implements Flash {
    private ScheduledTask leerTravelTask;
    private Location l;
    private EntityArmorStand as;
    public Leer(VPlayer player) {
        super(player);
        leerTravelTask = null;
        maxUses = 2;
        agent = Agent.REYNA;
        trigger = Trigger.RIGHT;
        l = null;
        as = null;
    }
    @Override
    public void use() {
        l = player.getEyeLocation().clone();
        as = Effects.getFlashArmorStand(l, agent);
        Effects.sendArmorStand(as, player.getMatch());
        leerTravelTask = Scheduler.repeat(() -> {
            Vector vel = l.getDirection().multiply(1 / 20);
            l.add(vel);
            Effects.flashTravel(l, agent);
            Effects.teleportArmorStand(l.clone().subtract(new Vector(0, player.getPlayer().getEyeHeight(), 0)), as, player.getMatch());
        }, 1L);
        Scheduler.delay(this::flash, 20L);
    }

    @Override
    public String getName() {
        return "Leer";
    }

    @Override
    public void remove() {
        Scheduler.cancel(leerTravelTask);
        Effects.removeArmorStand(as, player.getMatch());
    }

    @Override
    public ItemStack getShopDisplay() {
        return null;
    }

    @Override
    public Integer getPrice() {
        return 250;
    }

    @Override
    public void flash() {
        Scheduler.cancel(leerTravelTask);
        Effects.flashTravel(l, agent);
        this.remove();
        Collection<VPlayer> list = player.getMatch().getPlayers().values();
        for (VPlayer p : list) {
            if (p.getTeam() == player.getTeam()) return;
            if (Flash.isSeen(p.getPlayer(), as, (int) getFlashRange())) {
                Effects.flash(p.getPlayer(), agent, getFlashDuration());
                p.setFlashed(true);
                Scheduler.delay(() -> p.setFlashed(false), (long) (getFlashDuration() * 20L));
            }
        }
    }

    @Override
    public double getFlashDuration() {
        return 4.5;
    }

    @Override
    public double getFlashRange() {
        return 40;
    }
}
