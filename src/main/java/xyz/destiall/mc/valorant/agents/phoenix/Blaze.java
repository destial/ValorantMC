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

public class Blaze extends Ability {
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
        Effects.wall(player.getMatch(), player.getLocation(), direction, Agent.PHOENIX, 20, 4, 8);
        ItemStack a = player.getPlayer().getInventory().getItem(5);
        a.setAmount(a.getAmount() - 1);
    }

    @Override
    public String getName() {
        return "Blaze";
    }

    @Override
    public void remove() {

    }

    @Override
    public ItemStack getShopDisplay() {
        return item.clone();
    }

    @Override
    public Integer getPrice() {
        return 200;
    }
}
