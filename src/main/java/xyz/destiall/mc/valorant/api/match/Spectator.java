package xyz.destiall.mc.valorant.api.match;

import org.bukkit.entity.Player;

public interface Spectator {
    Player getPlayer();
    Match getMatch();
}
