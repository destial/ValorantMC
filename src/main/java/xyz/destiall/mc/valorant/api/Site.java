package xyz.destiall.mc.valorant.api;

import org.bukkit.util.BoundingBox;

public interface Site {
    BoundingBox getBounds();
    Type getSiteType();

    public enum Type {
        A,
        B,
        C,
        MID
    }
}
