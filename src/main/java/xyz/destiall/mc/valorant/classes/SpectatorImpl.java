package xyz.destiall.mc.valorant.classes;

import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Spectator;

public class SpectatorImpl implements Spectator {
    private final Player player;
    private final Match match;

    public SpectatorImpl(Player player, Match match) {
        this.player = player;
        this.match = match;
    }
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Match getMatch() {
        return match;
    }
}
