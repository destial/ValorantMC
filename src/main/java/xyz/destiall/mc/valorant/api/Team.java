package xyz.destiall.mc.valorant.api;

import java.util.List;

public interface Team {
    List<Participant> getMembers();
    Integer getScore();
    Match getMatch();
}
