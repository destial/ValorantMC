package xyz.destiall.mc.valorant.classes;

import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MatchImpl implements Match {
    private final Map map;
    private int round;
    private final List<Team> teams = new ArrayList<>();
    public MatchImpl(Map map) {
        this.map = map;
        round = 0;
    }

    @Override
    public List<Team> getTeams() {
        return teams;
    }

    @Override
    public Team getAttacker() {
        return teams.stream().filter(t -> t.getSide().equals(Team.Side.ATTACKER)).findFirst().orElse(null);
    }

    @Override
    public Team getDefender() {
        return teams.stream().filter(t -> t.getSide().equals(Team.Side.DEFENDER)).findFirst().orElse(null);
    }

    @Override
    public HashMap<UUID, Participant> getPlayers() {
        HashMap<UUID, Participant> players = new HashMap<>();
        for (Team team : teams) {
            for (Participant participant : team.getMembers()) {
                players.put(participant.getUUID(), participant);
            }
        }
        return players;
    }

    @Override
    public Integer getRound() {
        return round;
    }

    @Override
    public Map getMap() {
        return map;
    }

    @Override
    public void switchSides() {

    }

    @Override
    public void nextRound() {
        round++;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }
}
