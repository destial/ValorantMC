package xyz.destiall.mc.valorant.agents.phoenix;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.utils.Effects;

public class Blaze extends Ability {
    public Blaze() {
        agent = Agent.PHOENIX;
        maxUses = 1;
        hold = false;
    }
    @Override
    public void use(Player player, Vector direction) {
        Effects.wall(player.getLocation(), direction, Agent.PHOENIX, 20, 4, 8);
    }

    @Override
    public String getName() {
        return "Blaze";
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
        return 200;
    }
}
