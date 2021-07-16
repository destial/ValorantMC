package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.Ultimate;

public class BladeStorm extends Ultimate {
    public BladeStorm() {
        maxUses = -1;
        hold = true;
    }
    @Override
    public void use(Player player, Vector direction) {
        
    }

    @Override
    public String getName() {
        return "Blade Storm";
    }

    @Override
    public void update() {

    }

    @Override
    public ItemStack getShopDisplay() {
        return null;
    }

    @Override
    public Integer getPrice() {
        return null;
    }
}
