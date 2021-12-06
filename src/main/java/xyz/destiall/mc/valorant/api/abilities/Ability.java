package xyz.destiall.mc.valorant.api.abilities;

import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.items.ShopItem;
import xyz.destiall.mc.valorant.api.player.VPlayer;

public abstract class Ability implements ShopItem {
    protected Integer maxUses;
    protected Agent agent;
    protected Trigger trigger;
    protected VPlayer player;
    protected ItemStack item;
    protected boolean cancel;
    public Ability(VPlayer player) {
        this.player = player;
    }

    public abstract void use();
    public abstract String getName();
    public abstract void remove();

    public Integer getMaxUses() {
        return maxUses;
    }
    public Agent getBelongingAgent() { return agent; }
    public Trigger getTrigger() {
        return trigger;
    }
    public boolean cancelEvent() {
        return cancel;
    }

    public enum Trigger {
        RIGHT,
        LEFT,
        HOLD
    }
}
