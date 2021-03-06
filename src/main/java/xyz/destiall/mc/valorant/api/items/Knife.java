package xyz.destiall.mc.valorant.api.items;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.player.VPlayer;

public class Knife implements Giveable {
    protected final ItemStack itemStack;
    public Knife(ItemStack stack) {
        this.itemStack = stack;
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Knife");
        meta.setUnbreakable(true);
        stack.setItemMeta(meta);
    }

    @Override
    public ItemStack getItem() {
        return itemStack;
    }

    @Override
    public void give(VPlayer player) {
        player.getPlayer().getInventory().setItem(2, itemStack);
    }
}
