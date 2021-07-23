package xyz.destiall.mc.valorant.api.match;

import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.database.JSON;

public interface Round extends JSON {
    int getNumber();
    Team.Side getWinningSide();
    Team.Side getLosingSide();
    void setWinningSide(Team.Side side);
    void setLosingSide(Team.Side side);
}
