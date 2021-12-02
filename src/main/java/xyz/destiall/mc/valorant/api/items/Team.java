package xyz.destiall.mc.valorant.api.items;

import org.bukkit.Location;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.Collection;
import java.util.UUID;

public interface Team {
    Collection<VPlayer> getMembers();
    VPlayer getMember(UUID uuid);
    Integer getScore();
    Match getMatch();
    Side getSide();
    Location getSpawn();
    int getSize();

    void addMember(VPlayer member);
    void removeMember(UUID uuid);
    void addScore();
    void setSide(Side side);

    enum Side {
        ATTACKER,
        DEFENDER,
        FFA
    }
}
