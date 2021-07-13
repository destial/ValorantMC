package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.ShopItem;

public abstract class Ability implements ShopItem {
    protected Integer maxUses;
    protected int uses;
    protected int slot;
    protected Agent agent;
    public abstract void use(Participant participant, Vector direction);
    public abstract String getName();
    public abstract void update();

    public Integer getMaxUses() {
        return maxUses;
    }

    public int getSlot() { return slot; }
    public Agent getBelongingAgent() { return agent; }
}
