package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.player.Participant;
import xyz.destiall.mc.valorant.utils.Countdown;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Match {
    int getID();
    Set<Team> getTeams();
    Round getRound();
    List<Round> getRounds();
    Map getMap();
    Shop getShop();
    Countdown getCountdown();
    boolean isBuyPeriod();
    boolean isWaitingForPlayers();

    void switchSides();
    void endRound();
    void nextRound();
    boolean start();
    MatchResult end(MatchTerminateEvent.Reason reason);
    void terminate();
    void joinTeam(Team.Side side, Player player);
    void setCountdown(Countdown countdown);

    default boolean isComplete() {
        return getRound().getNumber() > 12 && (getAttacker().getScore() > getDefender().getScore() + 1
                || getDefender().getScore() > getAttacker().getScore() + 1);
    }
    default Team getWinningTeam() {
        if (!isComplete()) return null;
        return getTeams().stream().max(Comparator.comparingInt(Team::getScore)).orElse(null);
    }
    default boolean isInMatch(Player player) {
        return getPlayers().keySet().stream().anyMatch(k -> k.equals(player.getUniqueId()));
    }
    default HashMap<UUID, Participant> getPlayers() {
        HashMap<UUID, Participant> players = new HashMap<>();
        for (Team team : getTeams()) {
            for (Participant participant : team.getMembers()) {
                players.put(participant.getUUID(), participant);
            }
        }
        return players;
    }
    default void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
    default Team getAttacker() {
        return getTeams().stream().filter(t -> t.getSide().equals(Team.Side.ATTACKER)).findFirst().orElse(null);
    }
    default Team getDefender() {
        return getTeams().stream().filter(t -> t.getSide().equals(Team.Side.DEFENDER)).findFirst().orElse(null);
    }
}
