package xyz.destiall.mc.valorant.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.Shop;
import xyz.destiall.mc.valorant.factories.MatchFactory;

import java.util.HashSet;
import java.util.Set;

public class MatchManager {
    private final Set<Match> MATCHES = new HashSet<>();
    private Location lobby;
    private static MatchManager instance;

    public static MatchManager getInstance() {
        if (instance == null) {
            instance = new MatchManager();
            Shop.setup();
        }
        return instance;
    }

    private MatchManager() {
        instance = this;
        lobby = null;
    }

    public void disable() {
        for (Match match : MATCHES) {
            match.getShop().close();
            match.end();
        }
        MATCHES.clear();
    }

    public Location getLobby() {
        return lobby == null ? Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NORMAL).findFirst().get().getSpawnLocation() : lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public Match createNewMatch() {
        Map map = MapManager.getInstance().getRandomMap();
        if (map == null) return null;
        Match match = MatchFactory.createMatch(map);
        MATCHES.add(match);
        return match;
    }

    public Participant getParticipant(Player player) {
        Match match = MATCHES.stream().filter(m -> m.isInMatch(player)).findFirst().orElse(null);
        if (match == null) return null;
        return match.getPlayers().get(player.getUniqueId());
    }

    public Match getMatch(Player player) {
        Participant participant = getParticipant(player);
        if (participant == null) return null;
        return participant.getMatch();
    }

    public Match getMatch(int id) {
        return MATCHES.stream().filter(m -> m.getID() == id).findFirst().orElse(null);
    }

    public Match getMatch(Location location) {
        return MATCHES.stream().filter(m -> m.getMap().getBounds().contains(location.toVector())).findFirst().orElse(null);
    }

    public Match getMatch(Map map) {
        return MATCHES.stream().filter(m -> m.getMap() == map).findFirst().orElse(null);
    }

    public Set<Match> getAllMatches() {
        return MATCHES;
    }
}
