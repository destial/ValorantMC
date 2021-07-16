package xyz.destiall.mc.valorant.api.events.match;

import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.events.MatchEvent;

public class MatchInterruptEvent extends MatchEvent {
    public MatchInterruptEvent(Match match) {
        super(match);
    }
}
