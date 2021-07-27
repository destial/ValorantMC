package xyz.destiall.mc.valorant.classes;

import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.player.Participant;
import xyz.destiall.mc.valorant.api.player.Party;

import java.util.HashSet;
import java.util.Set;

public class PartyImpl implements Party {
    private final Participant leader;
    private final Set<Participant> members = new HashSet<>();
    public PartyImpl(Player leader) {
        Participant participant = new ParticipantImpl(leader, this);
        this.members.add(participant);
        this.leader = participant;
    }
    @Override
    public Set<Participant> getMembers() {
        return members;
    }

    @Override
    public Participant getLeader() {
        return leader;
    }

    @Override
    public void joinParty(Player player) {
        Participant participant = new ParticipantImpl(player, this);
        this.members.add(participant);
    }

    @Override
    public void disband() {

    }
}
