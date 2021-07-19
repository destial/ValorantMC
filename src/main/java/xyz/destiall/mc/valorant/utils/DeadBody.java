package xyz.destiall.mc.valorant.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import xyz.destiall.mc.valorant.api.Participant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeadBody {
    private static final HashMap<Participant, DeadBody> DEAD_BODIES = new HashMap<>();
    private final Set<ArmorStand> armorStands;
    private final Location location;
    private final Participant participant;
    public DeadBody(Participant participant) {
        this.participant = participant;
        this.location = participant.getPlayer().getLocation().clone();
        armorStands = new HashSet<>();
    }

    public void die() {
        participant.setDead(true);
        DEAD_BODIES.put(participant, this);
    }

    public void despawn() {
        for (ArmorStand as : armorStands) {
            as.remove();
        }
        armorStands.clear();
    }

    public Participant getBelongingPlayer() {
        return participant;
    }

    public void revive() {
        participant.setDead(false);
        participant.getPlayer().spigot().respawn();
        participant.getPlayer().teleport(location);
        despawn();
    }

    public static void clear() {
        for (DeadBody deadBody : DEAD_BODIES.values()) {
            deadBody.despawn();
        }
        DEAD_BODIES.clear();
    }
}
