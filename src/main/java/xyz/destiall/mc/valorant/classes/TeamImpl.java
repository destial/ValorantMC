package xyz.destiall.mc.valorant.classes;

import org.bukkit.Location;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.player.Participant;

import java.util.HashSet;
import java.util.Set;

public class TeamImpl implements Team {
    private final Set<Participant> members = new HashSet<>();
    private int score;
    private final Match match;
    private Side side;
    public TeamImpl(Match match, Side side) {
        this.side = side;
        this.match = match;
    }
    @Override
    public Set<Participant> getMembers() {
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
    public Location getSpawn() {
        return this.side == Side.ATTACKER ? match.getMap().getAttackerSpawn() : match.getMap().getDefenderSpawn();
    }

    @Override
    public void setSide(Side side) {
        this.side = side;
    }
}
