package xyz.destiall.mc.valorant.classes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.Team;
import xyz.destiall.mc.valorant.api.events.match.MatchCompleteEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchInterruptEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchStartEvent;
import xyz.destiall.mc.valorant.api.events.match.SwitchingSidesEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundFinishEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundStartEvent;
import xyz.destiall.mc.valorant.managers.MatchManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MatchImpl implements Match {
    private final Map map;
    private final int id;
    private int round;
    private final List<Team> teams = new ArrayList<>();
    public MatchImpl(Map map, int id) {
        this.id = id;
        this.map = map;
        teams.add(new TeamImpl(this, Team.Side.ATTACKER));
        teams.add(new TeamImpl(this, Team.Side.DEFENDER));
        round = 0;
    }

    @Override
    public int getID() {
        return id;
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
        Team attacker = getAttacker();
        Team defender = getDefender();
        if (attacker == defender) return;
        attacker.setSide(Team.Side.DEFENDER);
        defender.setSide(Team.Side.ATTACKER);
        for (Team team : teams) {
            for (Participant p : team.getMembers()) {
                p.applyDefaultSet();
            }
        }
        callEvent(new SwitchingSidesEvent(this));
    }

    @Override
    public void nextRound() {
        callEvent(new RoundFinishEvent(this));
        round++;
        callEvent(new RoundStartEvent(this));
    }

    @Override
    public void start() {
        MatchStartEvent e = new MatchStartEvent(this);
        callEvent(e);
        if (!e.isCancelled()) return;
        end();
    }

    @Override
    public void end() {
        map.setUse(false);
        Location loc = MatchManager.getInstance().getLobby();
        for (Participant p : getPlayers().values()) {
            p.getPlayer().getInventory().clear();
            p.getPlayer().teleport(loc);
        }
        teams.clear();
        if (isComplete()) {
            callEvent(new MatchCompleteEvent(this));
            return;
        }
        callEvent(new MatchInterruptEvent(this));
    }

    @Override
    public void joinTeam(Team.Side side, Player player) {
        Team team = teams.stream().filter(t -> t.getSide().equals(side)).findFirst().orElse(null);
        if (team == null) return;
        Participant participant = new ParticipantImpl(player, team);
        team.getMembers().add(participant);
    }
}
