package xyz.destiall.mc.valorant.classes;

import org.bukkit.Location;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class TeamImpl implements Team {
    private final HashMap<UUID, VPlayer> members = new HashMap<>();
    private int score;
    private final Match match;
    private Side side;
    public TeamImpl(Match match, Side side) {
        this.side = side;
        this.match = match;
    }
    @Override
    public Collection<VPlayer> getMembers() {
        return members.values();
    }

    @Override
    public void addMember(VPlayer member) {
        members.put(member.getUUID(), member);
    }

    @Override
    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    @Override
    public VPlayer getMember(UUID uuid) {
        return members.get(uuid);
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
    public int getSize() {
        return members.size();
    }

    @Override
    public void setSide(Side side) {
        this.side = side;
    }
}
