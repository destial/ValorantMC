package xyz.destiall.mc.valorant.api.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.player.Participant;

public class Knife implements Giveable {
    protected final ItemStack itemStack;
    public Knife(ItemStack stack) {
        this.itemStack = stack;
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Knife");
        stack.setItemMeta(meta);
    }

    @Override
    public ItemStack getItem() {
        return itemStack;
    }

    @Override
    public void give(Participant participant) {
        participant.getPlayer().getInventory().setItem(3, itemStack);
    }
}
