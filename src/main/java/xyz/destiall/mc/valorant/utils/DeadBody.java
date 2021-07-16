package xyz.destiall.mc.valorant.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import xyz.destiall.mc.valorant.api.Participant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeadBody {
    private static final HashMap<Participant, DeadBody> DEAD_BODIES = new HashMap<>();
    private final List<ArmorStand> armorStands;
    private final Location location;
    private Participant participant;
    public DeadBody(Participant participant) {
        this.participant = participant;
        this.location = participant.getPlayer().getLocation().clone();
        participant.setDead(true);
        armorStands = new ArrayList<>();
        DEAD_BODIES.put(participant, this);
    }

    public void die() {

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
