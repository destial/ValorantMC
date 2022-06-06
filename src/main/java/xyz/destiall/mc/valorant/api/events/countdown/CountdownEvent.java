package xyz.destiall.mc.valorant.api.events.countdown;

import xyz.destiall.mc.valorant.api.events.match.MatchEvent;
import xyz.destiall.mc.valorant.api.match.Countdown;
import xyz.destiall.mc.valorant.api.match.Match;

import java.time.Duration;

public abstract class CountdownEvent extends MatchEvent {
    private final Countdown countdown;
    public CountdownEvent(Match match, Countdown countdown) {
        super(match);
        this.countdown = countdown;
    }

    public Countdown getCountdown() {
        return countdown;
    }

    public Countdown.Context getContext() {
        return getCountdown().getContext();
    }

    public Duration getDuration() {
        return countdown.getRemaining();
    }
}
