package xyz.destiall.mc.valorant.factories;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Site;
import xyz.destiall.mc.valorant.classes.MapImpl;
import xyz.destiall.mc.valorant.classes.MatchImpl;
import xyz.destiall.mc.valorant.classes.SiteImpl;

public class MatchFactory {
    public static Match createMatch(Map map) {
        return new MatchImpl(map);
    }

    public static Map createMap(FileConfiguration yaml) {
        String worldName = yaml.getString("world");
        if (worldName == null) {
            worldName = Bukkit.getWorlds().get(0).getName();
        }
        World world = Bukkit.getWorld(worldName);
        BoundingBox bounds = new BoundingBox(yaml.getInt("xmn"), yaml.getInt("ymn"), yaml.getInt("zmn"), yaml.getInt("xmx"), yaml.getInt("ymx"), yaml.getInt("zmx"));
        return new MapImpl(world, bounds);
    }

    public static Site createSite(FileConfiguration yaml) {

        return new SiteImpl();
    }
}
