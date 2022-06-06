package xyz.destiall.mc.valorant.agents.reyna;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Flash;
import xyz.destiall.mc.valorant.api.abilities.PreviewHold;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class Leer extends Ability implements PreviewHold {
    private ScheduledTask leerTravelTask;
    private ScheduledTask holdTask;
    private Location l;
    private ArmorStand as;
    private ArmorStand hold;

    public Leer(VPlayer player) {
        super(player);
        leerTravelTask = null;
        holdTask = null;
        maxUses = 2;
        agent = Agent.REYNA;
        trigger = Trigger.RIGHT;
        l = null;
        as = null;
        cancel = true;
        item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + getName());
        item.setItemMeta(meta);
    }
    @Override
    public void use() {
        onStopHold();
        l = player.getEyeLocation();
        as = Effects.getFlashArmorStand(l, agent);
        Effects.sendArmorStand(as, player.getMatch(), agent);
        Vector dir = l.getDirection();
        AtomicBoolean flash = new AtomicBoolean(false);
        leerTravelTask = Scheduler.repeat(() -> {
            if (!flash.get()) l.add(dir);
            Effects.flashTravel(l, agent);
            Effects.teleportArmorStand(l, as, player.getMatch());
        }, 1L);
        Scheduler.delay(() -> {
            this.flash();
            flash.set(true);
        }, 20L);
    }

    @Override
    public String getName() {
        return "Leer";
    }

    @Override
    public void remove() {
        if (leerTravelTask != null) {
            Scheduler.cancel(leerTravelTask);
        }
        if (as != null) {
            Effects.removeArmorStand(as, player.getMatch());
        }
    }

    @Override
    public Integer getPrice() {
        return 250;
    }

    public void flash() {
        Scheduler.delay(this::remove, (long) (20L * 4.5));
        PotionEffect effect = PotionEffectType.BLINDNESS.createEffect((int) (20 * 4.5), 1);
        Collection<VPlayer> list = player.getMatch().getPlayers().values();
        for (VPlayer p : list) {
            if (p.getTeam() == player.getTeam()) return;
            if (Flash.isSeen(p.getPlayer(), as, 40)) {
                p.getPlayer().addPotionEffect(effect);
            }
        }
    }

    @Override
    public void onHold() {
        onStopHold();
        hold = Effects.getFlashArmorStand(player.getEyeLocation(), Agent.REYNA);
        Effects.sendArmorStand(hold, player);
        holdTask = Scheduler.repeat(() -> {
            Vector dir = player.getDirection();
            Location l = player.getEyeLocation().add(dir);
            Effects.teleportArmorStand(l, hold, player.getPlayer());

        }, 1L);
    }

    @Override
    public void onStopHold() {
        if (hold != null) {
            Effects.removeArmorStand(hold, player.getPlayer());
        }
        if (holdTask != null) {
            holdTask.cancel();
        }
    }
}
