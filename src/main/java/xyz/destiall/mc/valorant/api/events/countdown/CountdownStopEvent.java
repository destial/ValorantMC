package xyz.destiall.mc.valorant.api.events.countdown;

import xyz.destiall.mc.valorant.api.match.Countdown;
import xyz.destiall.mc.valorant.api.match.Match;

public class CountdownStopEvent extends CountdownEvent{
    public CountdownStopEvent(Match match, Countdown countdown) {
        super(match, countdown);
    }
}
