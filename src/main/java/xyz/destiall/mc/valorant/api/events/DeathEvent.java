package xyz.destiall.mc.valorant.api.events;

import xyz.destiall.mc.valorant.api.Participant;

public class DeathEvent extends MatchEvent {
    private final Participant victim;
    private final Participant killer;
    public DeathEvent(Participant victim, Participant killer) {
        super(victim.getTeam().getMatch());
        this.victim = victim;
        this.killer = killer;
    }

    public Participant getKiller() {
        return killer;
    }

    public Participant getVictim() {
        return victim;
    }
}
