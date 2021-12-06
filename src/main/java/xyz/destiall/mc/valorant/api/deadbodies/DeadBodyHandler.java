package xyz.destiall.mc.valorant.api.deadbodies;

import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Module;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.HashMap;

public class DeadBodyHandler implements Module {
    private final Match match;
    private final HashMap<VPlayer, DeadBody> bodies = new HashMap<>();
    public DeadBodyHandler(Match match) {
        this.match = match;
    }

    public void addBody(VPlayer player) {
        DeadBody body = new DeadBody(player);
        bodies.put(player, body);
        body.spawn();
    }

    @Override
    public void destroy() {
        for (DeadBody body : bodies.values()) {
            body.despawn();
        }
        bodies.clear();
    }
}
