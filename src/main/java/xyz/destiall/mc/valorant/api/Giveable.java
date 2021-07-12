package xyz.destiall.mc.valorant.api;

import org.bukkit.inventory.ItemStack;

public interface Giveable {
    ItemStack getItem();
    void give(Participant participant);
}
