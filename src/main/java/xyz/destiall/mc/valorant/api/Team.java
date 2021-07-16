package xyz.destiall.mc.valorant.api;

import java.util.List;

public interface Team {
    List<Participant> getMembers();
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
