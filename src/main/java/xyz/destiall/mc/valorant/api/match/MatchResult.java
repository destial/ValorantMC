package xyz.destiall.mc.valorant.api.match;

import org.json.JSONArray;
import org.json.JSONObject;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.classes.RoundImpl;
import xyz.destiall.mc.valorant.database.Datastore;
import xyz.destiall.mc.valorant.database.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchResult implements JSON {
    private final UUID uuid;
    private final List<Round> rounds;
    public MatchResult(Match match) {
        this.uuid = UUID.randomUUID();
        this.rounds = match.getRounds();
    }

    public MatchResult(String json) {
        JSONObject object = new JSONObject(json);
        this.uuid = UUID.fromString(object.getString("uuid"));
        JSONArray array = object.getJSONArray("rounds");
        this.rounds = new ArrayList<>();
        for (Object o : array) {
            JSONObject round = (JSONObject) o;
            int number = round.getInt("number");
            Team.Side winner = Team.Side.valueOf(round.getString("winners"));
            Team.Side loser = Team.Side.valueOf(round.getString("losers"));
            this.rounds.add(new RoundImpl(number, winner, loser));
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void save() {
        Datastore.getInstance().saveMatch(this);
    }

    @Override
    public String toJSON() {
        JSONObject object = new JSONObject();
        object.put("uuid", uuid.toString());
        JSONArray array = new JSONArray();
        for (Round round : this.rounds) {
            array.put(round.toJSON());
        }
        object.put("rounds", array);
        return object.toString();
    }
}
