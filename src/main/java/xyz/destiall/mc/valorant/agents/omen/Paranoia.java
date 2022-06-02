package xyz.destiall.mc.valorant.agents.omen;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

public class Paranoia extends Ability {
    private ScheduledTask travelTask;

    public Paranoia(VPlayer player) {
        super(player);
        item = new ItemStack(Material.INK_SAC);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + getName());
        item.setItemMeta(meta);
        maxUses = 1;
        trigger = Trigger.RIGHT;
    }

    @Override
    public void use() {
        final Vector direction = player.getDirection();
        final Location l = player.getEyeLocation();
        final Location origin = l.clone();
        PotionEffect effect = PotionEffectType.BLINDNESS.createEffect(25, 1);
        travelTask = Scheduler.repeat(() -> {
            l.add(direction);
            Effects.smokeTravel(l, Agent.OMEN);
            l.getWorld().getNearbyEntities(l, 4.5, 4.5, 4.5).stream()
                    .filter(e -> e instanceof LivingEntity && player.getPlayer() != e)
                    .forEach(e -> {
                        LivingEntity le = (LivingEntity) e;
                        if (player.getTeam().getMember(le.getUniqueId()) == null)
                            le.addPotionEffect(effect);
                    });
            if (l.distanceSquared(origin) >= (33 * 33)) {
                remove();
            }
        }, 1L);
        ItemStack inc = player.getPlayer().getInventory().getItem(5);
        inc.setAmount(inc.getAmount() - 1);
    }

    @Override
    public String getName() {
        return "Paranoia";
    }

    @Override
    public void remove() {
        if (travelTask != null) travelTask.cancel();
    }

    @Override
    public ItemStack getShopDisplay() {
        return item.clone();
    }

    @Override
    public Integer getPrice() {
        return 300;
    }
}
