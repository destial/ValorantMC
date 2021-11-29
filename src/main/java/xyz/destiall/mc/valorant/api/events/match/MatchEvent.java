package xyz.destiall.mc.valorant.api.events.match;

import xyz.destiall.mc.valorant.api.events.VEvent;
import xyz.destiall.mc.valorant.api.match.Match;

public class MatchEvent extends VEvent {
    private final Match match;
    public MatchEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }
}
