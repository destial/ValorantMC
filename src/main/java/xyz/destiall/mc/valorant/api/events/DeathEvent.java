package xyz.destiall.mc.valorant.api.events;

import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Knife;
import xyz.destiall.mc.valorant.api.player.Participant;

public class DeathEvent extends MatchEvent {
    private final Participant victim;
    private final Participant killer;
    private final Gun gun;
    private final Knife knife;
    public DeathEvent(Participant victim, Participant killer, Gun gun, Knife knife) {
        super(victim.getTeam().getMatch());
        this.victim = victim;
        this.killer = killer;
        this.gun = gun;
        this.knife = knife;
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

    public Participant getKiller() {
        return killer;
    }

    public Participant getVictim() {
        return victim;
    }
}
