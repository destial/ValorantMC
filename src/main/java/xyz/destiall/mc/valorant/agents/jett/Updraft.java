package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

public class Updraft extends Ability {
    private ScheduledTask slowFallingTask;

    public Updraft(VPlayer player) {
        super(player);
        maxUses = 2;
        agent = Agent.JETT;
        trigger = Trigger.HOLD;
        item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + getName());
        item.setItemMeta(meta);
    }

    @Override
    public void use() {
        Vector velocity = player.getPlayer().getVelocity();
        velocity.add(new Vector(0, 1f, 0));
        player.getPlayer().setVelocity(velocity);
        slowFallingTask = Scheduler.repeat(() -> {
            if (!player.getPlayer().isOnline()) {
                remove();
                return;
            }
            if (!player.getPlayer().isOnGround()) {
                player.getPlayer().addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(2, 1));
            } else {
                remove();
            }
        }, 10L);
    }

    @Override
    public String getName() {
        return "Updraft";
    }

    @Override
    public void remove() {
        if (slowFallingTask != null) slowFallingTask.cancel();
    }

    @Override
    public Integer getPrice() {
        return 150;
    }
}
