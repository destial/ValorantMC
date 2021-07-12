package xyz.destiall.mc.valorant.managers;

import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.agents.jett.Jett;
import xyz.destiall.mc.valorant.api.Agent;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {
    private final List<Match> matches = new ArrayList<>();
    private final List<Agent> agents = new ArrayList<>();
    private static MatchManager instance;
    public MatchManager() {
        instance = this;
        agents.add(new Jett());
    }

    public static MatchManager getInstance() {
        return instance;
    }

    public void startNewMatch(Map map) {

    }

    public List<Agent> getAgents() {
        return agents;
    }

    public Participant getMatchFromPlayer(Player player) {
        Match match = matches.stream().filter(m -> m.isInMatch(player)).findFirst().orElse(null);
        if (match == null) return null;
        return match.getPlayers().get(player.getUniqueId());
    }
}
