package xyz.destiall.mc.valorant.api.match;

import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.database.JSON;

public interface Round extends JSON {
    int getNumber();
    Team.Side getWinningSide();
    void setWinningSide(Team.Side side);
    boolean isOver();
}
