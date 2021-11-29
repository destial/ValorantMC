package xyz.destiall.mc.valorant.classes;

import org.json.JSONObject;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Round;
import xyz.destiall.mc.valorant.api.match.Spike;

public class RoundImpl implements Round {
    private final int round;
    private Spike spike;
    private Team.Side winner;
    private Team.Side loser;
    public RoundImpl(int round) {
        this.round = round;
    }
    public RoundImpl(int round, Team.Side winner, Team.Side loser) {
        this.round = round;
        this.winner = winner;
        this.loser = loser;
    }
    @Override
    public int getNumber() {
        return round;
    }

    @Override
    public Team.Side getWinningSide() {
        return winner;
    }

    @Override
    public Team.Side getLosingSide() {
        return loser;
    }

    @Override
    public void setWinningSide(Team.Side side) {
        this.winner = side;
    }

    @Override
    public void setLosingSide(Team.Side side) {
        this.loser = side;
    }

    @Override
    public String toJSON() {
        JSONObject object = new JSONObject();
        object.put("number", round);
        object.put("winners", winner.name());
        object.put("losers", loser.name());
        return object.toString();
    }
}
