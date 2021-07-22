package xyz.destiall.mc.valorant.api.events.match;

import xyz.destiall.mc.valorant.api.events.MatchEvent;
import xyz.destiall.mc.valorant.api.match.Match;

public class MatchCompleteEvent extends MatchEvent {
    public MatchCompleteEvent(Match match) {
        super(match);
    }
}
