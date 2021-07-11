package xyz.destiall.mc.valorant.api;

import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Match {
    List<Team> getTeams();
    HashMap<UUID, Participant> getPlayers();
    void callEvent(Event event);
    Integer getRound();
}
