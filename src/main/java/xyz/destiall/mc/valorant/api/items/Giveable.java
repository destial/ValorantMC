package xyz.destiall.mc.valorant.api.items;

import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.player.Participant;

public interface Giveable {
    ItemStack getItem();
    void give(Participant participant);
}
