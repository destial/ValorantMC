package xyz.destiall.mc.valorant.api.events.countdown;

import xyz.destiall.mc.valorant.api.match.Countdown;
import xyz.destiall.mc.valorant.api.match.Match;

public class CountdownStartEvent extends CountdownEvent{
    public CountdownStartEvent(Match match, Countdown countdown) {
        super(match, countdown);
    }
}
