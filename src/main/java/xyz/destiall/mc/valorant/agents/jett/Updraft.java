package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;

public class Updraft extends Ability {
    public Updraft() {
        maxUses = 2;
        agent = Agent.JETT;
        hold = false;
    }
    @Override
    public void use(Player player, Vector direction) {
        Vector velocity = player.getVelocity();
        velocity.add(new Vector(0, 1, 0));
        player.setVelocity(velocity);
    }

    @Override
    public String getName() {
        return "Updraft";
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
        return 150;
    }
}
