package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.player.Party;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Match extends Modular {
    int getID();
    Set<Team> getTeams();
    Round getRound();
    List<Round> getRounds();
    Map getMap();
    Spike getSpike();
    HashMap<Item, Gun> getDroppedGuns();
    boolean isBuyPeriod();
    boolean isWaitingForPlayers();

    void setCountdown(Countdown countdown);
    void switchSides();
    void endRound();
    void nextRound();
    boolean start(boolean force);
    MatchResult end(MatchTerminateEvent.Reason reason);
    void terminate();
    void join(Player player);
    void joinTeam(Team.Side side, Player player);
    void joinParty(Party party);
    MatchState getState();
    void setState(MatchState state);

    default boolean isComplete() {
        if (getRound() == null) return false;
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
    default HashMap<UUID, VPlayer> getPlayers() {
        HashMap<UUID, VPlayer> players = new HashMap<>();
        for (Team team : getTeams()) {
            for (VPlayer VPlayer : team.getMembers()) {
                players.put(VPlayer.getUUID(), VPlayer);
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

    enum MatchState {
        WAITING,
        PLAYING,
        ENDING
    }
}
