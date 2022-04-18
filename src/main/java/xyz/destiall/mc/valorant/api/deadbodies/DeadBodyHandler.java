package xyz.destiall.mc.valorant.api.deadbodies;

import xyz.destiall.mc.valorant.api.match.Module;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.HashMap;

public class DeadBodyHandler implements Module {
    private final HashMap<VPlayer, DeadBody> bodies = new HashMap<>();
    public DeadBodyHandler() {}

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
