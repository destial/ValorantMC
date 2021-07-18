package xyz.destiall.mc.valorant.api;

import org.bukkit.entity.Player;

public interface Spectator {
    Player getPlayer();
    Match getMatch();
}
