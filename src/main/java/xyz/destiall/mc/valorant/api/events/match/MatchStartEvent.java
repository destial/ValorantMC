package xyz.destiall.mc.valorant.api.events.match;

import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.events.MatchEvent;

public class MatchStartEvent extends MatchEvent {
    public MatchStartEvent(Match match) {
        super(match);
    }
}
