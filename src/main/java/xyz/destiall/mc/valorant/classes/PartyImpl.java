package xyz.destiall.mc.valorant.classes;

import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.player.Party;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.HashSet;
import java.util.Set;

public class PartyImpl implements Party {
    private final VPlayer leader;
    private final Set<VPlayer> members = new HashSet<>();
    public PartyImpl(Player leader) {
        VPlayer vPlayer = new VPlayerImpl(leader, this);
        this.members.add(vPlayer);
        this.leader = vPlayer;
    }
    @Override
    public Set<VPlayer> getMembers() {
        return members;
    }

    @Override
    public VPlayer getLeader() {
        return leader;
    }

    @Override
    public void joinParty(Player player) {
        VPlayer vPlayer = new VPlayerImpl(player, this);
        this.members.add(vPlayer);
    }

    @Override
    public void disband() {

    }
}
