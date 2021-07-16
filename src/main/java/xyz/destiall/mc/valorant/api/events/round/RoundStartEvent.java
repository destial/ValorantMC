package xyz.destiall.mc.valorant.api.events.round;

import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.events.MatchEvent;

public class RoundStartEvent extends MatchEvent {
    public RoundStartEvent(Match match) {
        super(match);
    }

    public int getRound() {
        return getMatch().getRound();
    }
}
