package xyz.destiall.mc.valorant.api.events.match;

import org.bukkit.event.Cancellable;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.events.MatchEvent;

public class MatchStartEvent extends MatchEvent implements Cancellable {
    private boolean cancelled;
    public MatchStartEvent(Match match) {
        super(match);
        cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
