package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.items.Drop;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.player.Party;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface Match extends Modular {
    int getID();
    Set<Team> getTeams();
    Round getRound();
    List<Round> getRounds();
    Map getMap();
    Spike getSpike();
    ConcurrentHashMap<Item, Drop> getDroppedItems();
    boolean isBuyPeriod();
    boolean isWaitingForPlayers();
    boolean isOver();

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

    default void sendMessage(String message) {
        Collection<VPlayer> list = getPlayers().values();
        list.forEach(p -> p.sendMessage(message));
    }

    default boolean isComplete() {
        if (getRound() == null) return false;
        return getRound().getNumber() > 12 &&
                (getAttacker().getScore() > 12 || getDefender().getScore() > 12) &&
                (getAttacker().getScore() > getDefender().getScore() + 1 ||
                 getDefender().getScore() > getAttacker().getScore() + 1);
    }

    default boolean isEmpty() {
        return !getTeams().stream().anyMatch(t -> t.getMembers().stream().anyMatch(m -> m.getPlayer().isOnline()));
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
            for (VPlayer p : team.getMembers()) {
                players.put(p.getUUID(), p);
            }
        }
        return players;
    }

    default VPlayer getPlayer(UUID uuid) {
        for (Team team : getTeams()) {
            if (team.getMember(uuid) != null) return team.getMember(uuid);
        }
        return null;
    }

    default void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    default Team getAttacker() {
        return getTeams().stream().filter(t -> t.getSide() == Team.Side.ATTACKER).findFirst().orElse(null);
    }
    default Team getDefender() {
        return getTeams().stream().filter(t -> t.getSide() == Team.Side.DEFENDER).findFirst().orElse(null);
    }

    default Team getOtherTeam(Team team) {
        return team.getSide() == Team.Side.ATTACKER ? getDefender() : getAttacker();
    }

    enum MatchState {
        WAITING,
        PLAYING,
        ENDING
    }
}
