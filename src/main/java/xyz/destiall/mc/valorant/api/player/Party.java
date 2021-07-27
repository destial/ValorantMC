package xyz.destiall.mc.valorant.api.player;

import org.bukkit.entity.Player;

import java.util.Set;

public interface Party {
    Set<Participant> getMembers();

    Participant getLeader();
    void joinParty(Player player);
    void disband();
}
