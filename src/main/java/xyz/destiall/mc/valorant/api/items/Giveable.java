package xyz.destiall.mc.valorant.api.items;

import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.player.VPlayer;

public interface Giveable {
    ItemStack getItem();
    void give(VPlayer VPlayer);
}
