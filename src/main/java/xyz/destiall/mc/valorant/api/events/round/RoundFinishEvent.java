package xyz.destiall.mc.valorant.api.events.round;

import xyz.destiall.mc.valorant.api.events.MatchEvent;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Round;

public class RoundFinishEvent extends MatchEvent {
    public RoundFinishEvent(Match match) {
        super(match);
    }

    public Round getRound() {
        return getMatch().getRound();
    }
}
