package xyz.destiall.mc.valorant.api;

import org.bukkit.inventory.ItemStack;

public abstract class Knife {
    protected ItemStack itemStack;
    protected abstract Knife give();
}
