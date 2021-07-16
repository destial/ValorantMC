package xyz.destiall.mc.valorant.api.events.match;

import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.events.MatchEvent;

public class SwitchingSidesEvent extends MatchEvent {
    public SwitchingSidesEvent(Match match) {
        super(match);
    }
}
