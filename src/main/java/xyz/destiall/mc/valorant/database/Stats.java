package xyz.destiall.mc.valorant.database;

import org.json.JSONArray;
import org.json.JSONObject;
import xyz.destiall.mc.valorant.api.match.MatchResult;
import xyz.destiall.mc.valorant.api.player.ParticipantMatchResult;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Stats implements JSON {
    private final UUID uuid;
    private int totalKills;
    private int totalDeaths;
    private int totalAssists;
    private int wins;
    private int loses;
    private double totalXP;
    private int level;
    private final Set<ParticipantMatchResult> matchResults = new HashSet<>();
    public Stats(UUID uuid) {
        this.uuid = uuid;
        totalKills = 0;
        totalDeaths = 0;
        totalAssists = 0;
        wins = 0;
        loses = 0;
        totalXP = 0;
        level = 1;
    }

    public void load(String json) {
        JSONObject object = new JSONObject(json);
        totalKills = object.getInt("kills");
        totalDeaths = object.getInt("deaths");
        totalAssists = object.getInt("assists");
        totalXP = object.getDouble("xp");
        wins = object.getInt("wins");
        loses = object.getInt("loses");
        level = object.getInt("level");
        JSONArray array = object.getJSONArray("matches");
        for (Object o : array) {
            JSONObject ob = (JSONObject) o;
            ParticipantMatchResult result = new ParticipantMatchResult(ob.toString());
            matchResults.add(result);
        }
    }

    public void addWin(VPlayer player, MatchResult result) {
        ParticipantMatchResult r = new ParticipantMatchResult(player, result, true);
        matchResults.add(r);
        wins++;
    }

    public void addLoss(VPlayer player, MatchResult result) {
        ParticipantMatchResult r = new ParticipantMatchResult(player, result, false);
        matchResults.add(r);
        loses++;
    }

    public void addKill() {
        totalKills++;
    }

    public void addDeath() {
        totalDeaths++;
    }

    public void addAssist() {
        totalAssists++;
    }

    public void addXP(double xp) {
        totalXP += xp;
    }

    public Set<ParticipantMatchResult> getAllResults() {
        return matchResults;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toJSON() {
        JSONObject object = new JSONObject();
        object.put("uuid", uuid.toString());
        object.put("kills", totalKills);
        object.put("deaths", totalDeaths);
        object.put("assists", totalAssists);
        object.put("wins", wins);
        object.put("loses", loses);
        object.put("xp", totalXP);
        object.put("level", level);
        JSONArray array = new JSONArray();
        for (ParticipantMatchResult result : matchResults) {
            array.put(result.toJSON());
        }
        object.put("matches", array);
        return object.toString();
    }
}
