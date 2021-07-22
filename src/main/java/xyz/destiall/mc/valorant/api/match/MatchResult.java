package xyz.destiall.mc.valorant.api.match;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchResult {
    private final UUID uuid;
    private final List<Round> rounds;
    public MatchResult(Match match) {
        this.uuid = UUID.randomUUID();
        this.rounds = match.getRounds();
    }
    private MatchResult(UUID uuid, String... rounds) {
        this.uuid = uuid;
        this.rounds = new ArrayList<>();
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<Round> getRounds() {
        return rounds;
    }
}
