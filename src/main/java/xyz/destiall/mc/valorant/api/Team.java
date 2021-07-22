package xyz.destiall.mc.valorant.api;

import org.bukkit.Location;

import java.util.Set;

public interface Team {
    Set<Participant> getMembers();
    Integer getScore();
    void addScore();
    Match getMatch();
    Side getSide();
    Location getSpawn();
    void setSide(Side side);
    enum Side {
        ATTACKER,
        DEFENDER
    }
}
