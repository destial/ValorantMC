package xyz.destiall.mc.valorant.api.events.match;

import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.events.MatchEvent;

public class MatchCompleteEvent extends MatchEvent {
    public MatchCompleteEvent(Match match) {
        super(match);
    }
}
