package xyz.destiall.mc.valorant.agents.omen;

import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.player.VPlayer;

public class ShroudedStep extends Ability {
    public ShroudedStep(VPlayer player) {
        super(player);
    }

    @Override
    public void use() {

    }

    @Override
    public String getName() {
        return "Shrouded Step";
    }

    @Override
    public void remove() {

    }

    @Override
    public ItemStack getShopDisplay() {
        return null;
    }

    @Override
    public Integer getPrice() {
        return 100;
    }
}
