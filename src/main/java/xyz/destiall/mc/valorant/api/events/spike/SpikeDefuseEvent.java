package xyz.destiall.mc.valorant.api.events.spike;

import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Spike;
import xyz.destiall.mc.valorant.api.events.MatchEvent;

public class SpikeDefuseEvent extends MatchEvent {
    private final Spike spike;

    public SpikeDefuseEvent(Spike spike) {
        super(spike.getMatch());
        this.spike = spike;
    }

    public Match getMatch() {
        return spike.getMatch();
    }
}
