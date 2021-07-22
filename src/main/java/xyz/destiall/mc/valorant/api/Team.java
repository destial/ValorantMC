package xyz.destiall.mc.valorant.api;

import java.util.Set;

public interface Team {
    Set<Participant> getMembers();
    Integer getScore();
    void addScore();
    Match getMatch();
    Side getSide();
    void setSide(Side side);
    enum Side {
        ATTACKER,
        DEFENDER
    }
}
