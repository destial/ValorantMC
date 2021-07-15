package xyz.destiall.mc.valorant.factories;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Site;
import xyz.destiall.mc.valorant.classes.MapImpl;
import xyz.destiall.mc.valorant.classes.MatchImpl;
import xyz.destiall.mc.valorant.classes.SiteImpl;

import java.util.ArrayList;
import java.util.List;

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
        float spawnRadius = (float) yaml.getDouble("spawn.radius");
        Location attacker = new Location(world, yaml.getDouble("spawn.attacker.x"), yaml.getDouble("spawn.attacker.y"), yaml.getDouble("spawn.attacker.z"), (float) yaml.getDouble("spawn.attacker.yaw"), (float) yaml.getDouble("spawn.attacker.pitch"));
        Location defender = new Location(world, yaml.getDouble("spawn.defender.x"), yaml.getDouble("spawn.defender.y"), yaml.getDouble("spawn.defender.z"), (float) yaml.getDouble("spawn.defender.yaw"), (float) yaml.getDouble("spawn.defender.pitch"));
        List<Site> sites = new ArrayList<>();
        for (String key : yaml.getConfigurationSection("sites").getKeys(false)) {
            if (key.equalsIgnoreCase("A")) {
                sites.add(createSite(Site.Type.A, (FileConfiguration) yaml.getConfigurationSection("sites." + key)));
            }
        }
        return new MapImpl(world, bounds, spawnRadius, attacker, defender, sites);
    }

    public static Site createSite(Site.Type type, FileConfiguration yaml) {
        BoundingBox bounds = new BoundingBox(yaml.getInt("xmn"), yaml.getInt("ymn"), yaml.getInt("zmn"), yaml.getInt("xmx"), yaml.getInt("ymx"), yaml.getInt("zmx"));
        return new SiteImpl(type, bounds);
    }
}
