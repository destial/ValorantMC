package xyz.destiall.mc.valorant.api.map;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;

public interface Site {
    BoundingBox getBounds();
    Type getSiteType();
    default void render(VPlayer player, Map map) {
        Location location = new Location(map.getWorld(), getBounds().getMinX(), getBounds().getCenterY(), getBounds().getMinZ());
        for (double x = getBounds().getMinX(); x <= getBounds().getMaxX(); ++x) {
            location.setX(x);
            Effects.showDust(player, location, Color.WHITE);
        }
        location.setX(getBounds().getMinX());
        for (double z = getBounds().getMinZ(); z <= getBounds().getMaxZ(); ++z) {
            location.setZ(z);
            Effects.showDust(player, location, Color.WHITE);
        }
        location.setZ(getBounds().getMaxZ());
        for (double x = getBounds().getMinX(); x <= getBounds().getMaxX(); ++x) {
            location.setX(x);
            Effects.showDust(player, location, Color.WHITE);
        }
        location.setX(getBounds().getMaxX());
        for (double z = getBounds().getMinZ(); z <= getBounds().getMaxZ(); ++z) {
            location.setZ(z);
            Effects.showDust(player, location, Color.WHITE);
        }
    }

    enum Type {
        A,
        B,
        C,
        MID
    }
}
