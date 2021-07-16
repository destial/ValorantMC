package xyz.destiall.mc.valorant.api.events.round;

import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.events.MatchEvent;

public class RoundFinishEvent extends MatchEvent {
    public RoundFinishEvent(Match match) {
        super(match);
    }

    public int getRound() {
        return getMatch().getRound();
    }
}
