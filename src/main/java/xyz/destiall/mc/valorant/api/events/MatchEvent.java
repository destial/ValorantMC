package xyz.destiall.mc.valorant.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.destiall.mc.valorant.api.Match;

public class MatchEvent extends Event {
    private final Match match;
    public MatchEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    public static final HandlerList handlerList = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
