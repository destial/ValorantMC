package xyz.destiall.mc.valorant.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.factories.MatchFactory;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {
    private final List<Match> matches = new ArrayList<>();
    private Location lobby;
    private static MatchManager instance;

    public MatchManager() {
        instance = this;
        lobby = null;
    }

    public void disable() {
        for (Match match : matches) {
            match.end();
        }
        matches.clear();
    }

    public Location getLobby() {
        return lobby == null ? Bukkit.getWorld("world").getSpawnLocation() : lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public static MatchManager getInstance() {
        return instance;
    }

    public Match createNewMatch() {
        Map map = MapManager.getInstance().getRandomMap();
        if (map == null) return null;
        Match match = MatchFactory.createMatch(map);
        matches.add(match);
        return match;
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

    public Match getMatch(int id) {
        return matches.stream().filter(m -> m.getID() == id).findFirst().orElse(null);
    }

    public List<Match> getAllMatches() {
        return matches;
    }
}
