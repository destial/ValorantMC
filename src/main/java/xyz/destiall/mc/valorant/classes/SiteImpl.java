package xyz.destiall.mc.valorant.classes;

import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.Site;

public class SiteImpl implements Site {
    private final BoundingBox bounds;
    private final Type siteType;
    public SiteImpl(Type type, int xmn, int zmn, int xmx, int zmx) {
        this.siteType = type;
        bounds = new BoundingBox(xmn, 0, zmn, xmx, 256, zmx);
    }
    @Override
    public BoundingBox getBounds() {
        return bounds;
    }

    @Override
    public Type getSiteType() {
        return siteType;
    }
}
