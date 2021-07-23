package xyz.destiall.mc.valorant.api.player;

import org.json.JSONObject;
import xyz.destiall.mc.valorant.api.match.MatchResult;
import xyz.destiall.mc.valorant.database.JSON;

import java.util.UUID;

public class ParticipantMatchResult implements JSON {
    private final UUID matchUUID;
    private final UUID participant;
    private final boolean win;
    public ParticipantMatchResult(UUID participant, MatchResult result, boolean win) {
        this.matchUUID = result.getUUID();
        this.participant = participant;
        this.win = win;
    }

    public ParticipantMatchResult(String json) {
        JSONObject object = new JSONObject(json);
        this.matchUUID = UUID.fromString(object.getString("match"));
        this.participant = UUID.fromString(object.getString("participant"));
        this.win = object.getBoolean("win");
    }

    public UUID getMatchUUID() {
        return matchUUID;
    }

    public UUID getParticipantUUID() {
        return participant;
    }

    public boolean isWin() {
        return win;
    }

    @Override
    public String toJSON() {
        JSONObject object = new JSONObject();
        object.put("participant", participant.toString());
        object.put("match", matchUUID.toString());
        object.put("win", win);
        return object.toString();
    }
}
