package xyz.destiall.mc.valorant.api.events;

import xyz.destiall.mc.valorant.api.Match;

public class RoundStartEvent extends MatchEvent {
    public RoundStartEvent(Match match) {
        super(match);
    }

    public int getRound() {
        return getMatch().getRound();
    }
}
