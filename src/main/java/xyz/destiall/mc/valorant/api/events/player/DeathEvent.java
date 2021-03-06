package xyz.destiall.mc.valorant.api.events.player;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.events.match.MatchEvent;
import xyz.destiall.mc.valorant.api.items.Drop;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Knife;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.HashMap;

public class DeathEvent extends MatchEvent {
    private final VPlayer victim;
    private final VPlayer killer;
    private final Gun gun;
    private final Knife knife;
    private final HashMap<Item, Drop> drops;
    private final boolean headshot;
    private final boolean sniper;

    public DeathEvent(VPlayer victim, VPlayer killer, Gun gun, Knife knife, boolean headshot, boolean sniper) {
        super(victim.getTeam().getMatch());
        this.victim = victim;
        this.killer = killer;
        this.gun = gun;
        this.knife = knife;
        this.headshot = headshot;
        this.sniper = sniper;
        drops = new HashMap<>();
    }

    public boolean isSniper() {
        return sniper;
    }

    public boolean isHeadshot() {
        return headshot;
    }

    public boolean isSuicide() {
        return gun != null && knife != null;
    }

    public Gun getGun() {
        return gun;
    }

    public Knife getKnife() {
        return knife;
    }

    public VPlayer getKiller() {
        return killer;
    }

    public VPlayer getVictim() {
        return victim;
    }

    public HashMap<Item, Drop> getDrops() {
        return drops;
    }
}
