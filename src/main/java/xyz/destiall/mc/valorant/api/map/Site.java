package xyz.destiall.mc.valorant.api.map;

import org.bukkit.util.BoundingBox;

public interface Site {
    BoundingBox getBounds();
    Type getSiteType();

    enum Type {
        A,
        B,
        C,
        MID
    }
}
