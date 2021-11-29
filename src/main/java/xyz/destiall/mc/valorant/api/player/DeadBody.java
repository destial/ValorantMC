package xyz.destiall.mc.valorant.api.player;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DeadBody {
    private static final HashMap<VPlayer, DeadBody> DEAD_BODIES = new HashMap<>();
    private final Set<ArmorStand> armorStands;
    private final Location location;
    private final VPlayer vPlayer;
    public DeadBody(VPlayer vPlayer) {
        this.vPlayer = vPlayer;
        this.location = vPlayer.getPlayer().getLocation().clone();
        armorStands = new HashSet<>();
    }

    public void die() {
        vPlayer.setDead(true);
        DEAD_BODIES.put(vPlayer, this);
    }

    public void despawn() {
        for (ArmorStand as : armorStands) {
            as.remove();
        }
        armorStands.clear();
    }

    public VPlayer getBelongingPlayer() {
        return vPlayer;
    }

    public void revive() {
        vPlayer.setDead(false);
        vPlayer.getPlayer().spigot().respawn();
        vPlayer.getPlayer().teleport(location);
        despawn();
    }

    public static void clear() {
        for (DeadBody deadBody : DEAD_BODIES.values()) {
            deadBody.despawn();
        }
        DEAD_BODIES.clear();
    }
}
