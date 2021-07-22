package xyz.destiall.mc.valorant.api.events.round;

import xyz.destiall.mc.valorant.api.events.MatchEvent;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Round;

public class RoundStartEvent extends MatchEvent {
    public RoundStartEvent(Match match) {
        super(match);
    }

    public Round getRound() {
        return getMatch().getRound();
    }
}
