package xyz.destiall.mc.valorant.managers;

import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.factories.MatchFactory;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {
    private final List<Match> matches = new ArrayList<>();
    private static MatchManager instance;
    public MatchManager() {
        instance = this;
    }

    public void disable() {

    }

    public static MatchManager getInstance() {
        return instance;
    }

    public boolean startNewMatch() {
        Map map = MapManager.getInstance().getRandomMap();
        if (map == null) return false;
        Match match = MatchFactory.createMatch(map);
        matches.add(match);
        return true;
    }

    public boolean endMatch(Match match) {
        match.end();
        return true;
    }

    public Participant getParticipant(Player player) {
        Match match = matches.stream().filter(m -> m.isInMatch(player)).findFirst().orElse(null);
        if (match == null) return null;
        return match.getPlayers().get(player.getUniqueId());
    }

    public Match getMatch(Player player) {
        Participant participant = getParticipant(player);
        if (participant == null) return null;
        return participant.getMatch();
    }
}
