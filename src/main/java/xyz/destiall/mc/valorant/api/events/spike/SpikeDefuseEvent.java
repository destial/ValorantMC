package xyz.destiall.mc.valorant.api.events.spike;

import xyz.destiall.mc.valorant.api.events.MatchEvent;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Spike;

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
