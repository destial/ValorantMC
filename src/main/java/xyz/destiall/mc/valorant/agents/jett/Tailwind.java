package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

public class Tailwind extends Ability implements Listener {
    private Vector previousVelocity;

    public Tailwind(VPlayer player) {
        super(player);
        maxUses = 2;
        agent = Agent.JETT;
        trigger = Trigger.HOLD;
        item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + getName());
        item.setItemMeta(meta);
    }

    @Override
    public void use() {
        Vector velocity = (previousVelocity != null && player.getPlayer().getVelocity().length() == 0) ? previousVelocity : player.getPlayer().getVelocity();
        try {
            velocity.setY(0.01);
            velocity.normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        player.getPlayer().setVelocity(velocity);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer() == player.getPlayer()) {
            previousVelocity = e.getPlayer().getVelocity();
        }
    }

    @Override
    public String getName() {
        return "Tailwind";
    }

    @Override
    public void remove() {

    }

    @Override
    public Integer getPrice() {
        return 150;
    }
}
