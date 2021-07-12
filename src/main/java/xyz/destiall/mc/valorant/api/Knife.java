package xyz.destiall.mc.valorant.api;

import org.bukkit.inventory.ItemStack;

public class Knife implements Giveable {
    protected final ItemStack itemStack;
    public Knife(ItemStack stack) {
        this.itemStack = stack;
    }

    @Override
    public ItemStack getItem() {
        return itemStack;
    }

    @Override
    public void give(Participant participant) {

    }
}
