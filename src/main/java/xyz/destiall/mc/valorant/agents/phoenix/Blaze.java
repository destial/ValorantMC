package xyz.destiall.mc.valorant.agents.phoenix;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;

public class Blaze extends Ability {
    private ScheduledTask wallTask;

    public Blaze(VPlayer player) {
        super(player);
        agent = Agent.PHOENIX;
        maxUses = 1;
        trigger = Trigger.RIGHT;
        item = new ItemStack(Material.MAGMA_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + getName());
        item.setItemMeta(meta);
    }
    @Override
    public void use() {
        Vector direction = player.getDirection();
        wallTask = Effects.wall(player.getMatch(), player.getLocation(), direction, Agent.PHOENIX, 20, 4, 8);
    }

    @Override
    public String getName() {
        return "Blaze";
    }

    @Override
    public void remove() {
        wallTask.setRunOnCancel(true);
        wallTask.cancel();
        wallTask = null;
    }

    @Override
    public Integer getPrice() {
        return 200;
    }
}
