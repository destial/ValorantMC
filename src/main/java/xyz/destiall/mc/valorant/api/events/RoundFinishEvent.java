package xyz.destiall.mc.valorant.api.events;

import xyz.destiall.mc.valorant.api.Match;

public class RoundFinishEvent extends MatchEvent {
    public RoundFinishEvent(Match match) {
        super(match);
    }

    public int getRound() {
        return getMatch().getRound();
    }
}
