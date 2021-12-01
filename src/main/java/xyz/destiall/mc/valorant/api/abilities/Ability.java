package xyz.destiall.mc.valorant.api.abilities;

import xyz.destiall.mc.valorant.api.items.ShopItem;
import xyz.destiall.mc.valorant.api.player.VPlayer;

public abstract class Ability implements ShopItem {
    protected Integer maxUses;
    protected Agent agent;
    protected int slot;
    protected boolean hold;
    protected VPlayer player;
    public Ability(VPlayer player) {
        this.player = player;
    }

    public abstract void use();
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
