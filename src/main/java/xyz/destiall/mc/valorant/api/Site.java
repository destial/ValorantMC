package xyz.destiall.mc.valorant.api;

import org.bukkit.util.BoundingBox;

public abstract class Site {
    protected Type siteType;
    public abstract BoundingBox getBounds();
    public Type getSiteType() { return siteType; }

    public enum Type {
        A,
        B,
        C,
        MID
    }
}
