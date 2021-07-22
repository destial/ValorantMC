package xyz.destiall.mc.valorant.api.events.spike;

import xyz.destiall.mc.valorant.api.events.MatchEvent;
import xyz.destiall.mc.valorant.api.match.Spike;

public class SpikePlaceEvent extends MatchEvent {
    private final Spike spike;
    public SpikePlaceEvent(Spike spike) {
        super(spike.getMatch());
        this.spike = spike;
    }

    public Spike getSpike() {
        return spike;
    }
}
