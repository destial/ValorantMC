package xyz.destiall.mc.valorant.api.events.match;

import xyz.destiall.mc.valorant.api.match.Match;

public class SwitchingSidesEvent extends MatchEvent {
    public SwitchingSidesEvent(Match match) {
        super(match);
    }
}
