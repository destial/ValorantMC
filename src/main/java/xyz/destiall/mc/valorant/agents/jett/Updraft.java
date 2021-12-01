package xyz.destiall.mc.valorant.agents.jett;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.player.VPlayer;

public class Updraft extends Ability {
    public Updraft(VPlayer player) {
        super(player);
        maxUses = 2;
        agent = Agent.JETT;
        hold = false;
    }
    @Override
    public void use() {
        Vector velocity = player.getPlayer().getVelocity();
        velocity.add(new Vector(0, 1, 0));
        player.getPlayer().setVelocity(velocity);
    }

    @Override
    public String getName() {
        return "Updraft";
    }

    @Override
    public void remove() {}

    @Override
    public ItemStack getShopDisplay() {
        return null;
    }

    @Override
    public Integer getPrice() {
        return 150;
    }
}
