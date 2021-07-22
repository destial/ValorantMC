package xyz.destiall.mc.valorant.api.match;

import xyz.destiall.mc.valorant.api.items.Team;

public interface Round {
    int getNumber();
    Team.Side getWinningSide();
    Team.Side getLosingSide();
    void setWinningSide(Team.Side side);
    void setLosingSide(Team.Side side);
}
