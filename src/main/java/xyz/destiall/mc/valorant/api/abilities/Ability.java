package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.ShopItem;

public abstract class Ability implements ShopItem {
    protected Integer maxUses;
    protected int uses;
    protected int slot;
    protected Agent agent;
    protected boolean hold;

    public abstract void use(Player player, Vector direction);
    public abstract String getName();
    public abstract void remove();

    public Integer getMaxUses() {
        return maxUses;
    }
    public int getSlot() { return slot; }
    public Agent getBelongingAgent() { return agent; }
    public boolean canHold() {
        return hold;
    }
}
