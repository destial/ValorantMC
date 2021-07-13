package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.abilities.Ability;

public class Updraft extends Ability {
    public Updraft() {
        maxUses = 2;
    }
    @Override
    public void use(Participant participant, Vector direction) {

    }

    @Override
    public String getName() {
        return "Updraft";
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
        return 150;
    }
}
