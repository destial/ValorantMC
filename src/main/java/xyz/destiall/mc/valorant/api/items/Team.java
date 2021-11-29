package xyz.destiall.mc.valorant.api.items;

import org.bukkit.Location;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.Set;

public interface Team {
    Set<VPlayer> getMembers();
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
