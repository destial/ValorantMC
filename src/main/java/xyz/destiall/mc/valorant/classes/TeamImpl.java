package xyz.destiall.mc.valorant.classes;

import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamImpl implements Team {
    private final List<Participant> members = new ArrayList<>();
    private int score;
    private final Match match;
    private Side side;
    public TeamImpl(Match match, Side side) {
        this.side = side;
        this.match = match;
    }
    @Override
    public List<Participant> getMembers() {
        return members;
    }

    @Override
    public Integer getScore() {
        return score;
    }

    @Override
    public void addScore() {
        score++;
    }

    @Override
    public Match getMatch() {
        return match;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public void setSide(Side side) {
        this.side = side;
    }
}
