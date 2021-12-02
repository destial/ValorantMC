package xyz.destiall.mc.valorant.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.MatchResult;
import xyz.destiall.mc.valorant.api.match.Shop;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.database.Datastore;
import xyz.destiall.mc.valorant.factories.MatchFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        lobby = null;
    }

    public void disable() {
        for (Match match : MATCHES) {
            match.end(MatchTerminateEvent.Reason.FORCE);
        }
        MATCHES.clear();
    }

    public Location getLobby() {
        return lobby == null ? Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NORMAL).findFirst().orElse(Bukkit.getWorlds().get(0)).getSpawnLocation() : lobby;
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

    public Match getEmptyMatch() {
        Match match = MATCHES.stream().filter(m -> m.getState().equals(Match.MatchState.WAITING) || m.isWaitingForPlayers()).findFirst().orElse(null);
        if (match != null) return match;
        return createNewMatch();
    }

    public VPlayer getParticipant(Player player) {
        Match match = MATCHES.stream().filter(m -> m.isInMatch(player)).findFirst().orElse(null);
        if (match == null) return null;
        return match.getPlayer(player.getUniqueId());
    }

    public Match getMatch(Player player) {
        VPlayer VPlayer = getParticipant(player);
        if (VPlayer == null) return null;
        return VPlayer.getMatch();
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

    public MatchResult getMatchResult(UUID uuid) {
        String data = Datastore.getInstance().loadMatch(uuid);
        if (data == null) return null;
        return new MatchResult(data);
    }
}
