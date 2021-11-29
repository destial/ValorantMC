package xyz.destiall.mc.valorant.api.events.match;

import xyz.destiall.mc.valorant.api.match.Match;

public class MatchTerminateEvent extends MatchEvent {
    private final Reason reason;
    public MatchTerminateEvent(Match match, Reason reason) {
        super(match);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }

    public enum Reason {
        HACK("A hacker was detected in your match!"),
        FORCE("Forcefully terminated your match!"),
        COMPLETE("This match was completed!");

        private final String reason;
        Reason(String reason) {
            this.reason = reason;
        }

        @Override
        public String toString() {
            return reason;
        }
    }
}
