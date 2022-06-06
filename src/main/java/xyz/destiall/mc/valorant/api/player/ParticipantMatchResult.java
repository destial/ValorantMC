package xyz.destiall.mc.valorant.api.player;

import org.json.JSONObject;
import xyz.destiall.mc.valorant.api.match.MatchResult;
import xyz.destiall.mc.valorant.database.JSON;

import java.util.UUID;

public class ParticipantMatchResult implements JSON {
    private final UUID matchUUID;
    private final UUID participant;
    private final boolean win;
    private final int kills;
    private final int deaths;
    private final int assists;

    public ParticipantMatchResult(VPlayer player, MatchResult result, boolean win) {
        this.matchUUID = result.getUUID();
        this.participant = player.getUUID();
        this.win = win;
        this.kills = player.getKills();
        this.deaths = player.getDeaths();
        this.assists = player.getAssists();
    }

    public ParticipantMatchResult(String json) {
        JSONObject object = new JSONObject(json);
        this.matchUUID = UUID.fromString(object.getString("match"));
        this.participant = UUID.fromString(object.getString("participant"));
        this.win = object.getBoolean("win");
        this.kills = object.getInt("kills");
        this.deaths = object.getInt("deaths");
        this.assists = object.getInt("assists");
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
    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("participant", participant.toString());
        object.put("match", matchUUID.toString());
        object.put("win", win);
        object.put("kills", kills);
        object.put("deaths", deaths);
        object.put("assists", assists);
        return object;
    }
}
