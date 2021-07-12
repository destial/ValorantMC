package xyz.destiall.mc.valorant.api;

import org.bukkit.util.Vector;

public abstract class Ability implements ShopItem {
    protected Integer uses;
    protected Integer maxUses;
    protected int slot;
    public abstract void use(Participant participant, Vector direction);
    public Integer getUses() {
        return uses;
    }

    public Integer getMaxUses() {
        return maxUses;
    }

    public int getSlot() { return slot; }
}
