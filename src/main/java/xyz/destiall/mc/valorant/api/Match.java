package xyz.destiall.mc.valorant.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Match {
    List<Team> getTeams();
    Team getAttacker();
    Team getDefender();
    HashMap<UUID, Participant> getPlayers();
    Integer getRound();
    Map getMap();
    void switchSides();
    void nextRound();
    void start();
    void end();
    default boolean isComplete() {
        return getRound() > 12 && (getAttacker().getScore() > getDefender().getScore() + 1
                || getDefender().getScore() > getAttacker().getScore() + 1);
    }

    default Team getWinningTeam() {
        if (!isComplete()) return null;
        return getTeams().stream().sorted((a, b) -> a.getScore() > b.getScore() ? 1 : 0).findFirst().orElse(null);
    }

    default boolean isInMatch(Player player) {
        return getPlayers().keySet().stream().anyMatch(k -> k.equals(player.getUniqueId()));
    }

    default void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
