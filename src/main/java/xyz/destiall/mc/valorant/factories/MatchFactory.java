package xyz.destiall.mc.valorant.factories;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
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
    private static int MATCHES = 0;
    public static Match createMatch(Map map) {
        return new MatchImpl(map, ++MATCHES);
    }

    public static Map createMap(FileConfiguration yaml) {
        String worldName = yaml.getString("world");
        String mapName = yaml.getString("name", "A Map");
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
                sites.add(createSite(Site.Type.A, yaml.getConfigurationSection("sites." + key)));
            } else if (key.equalsIgnoreCase("B")) {
                sites.add(createSite(Site.Type.B, yaml.getConfigurationSection("sites." + key)));
            } else if (key.equalsIgnoreCase("C")) {
                sites.add(createSite(Site.Type.C, yaml.getConfigurationSection("sites." + key)));
            }
        }
        return new MapImpl(mapName.trim(), world, bounds, spawnRadius, attacker, defender, sites);
    }

    public static Site createSite(Site.Type type, ConfigurationSection yaml) {
        BoundingBox bounds = new BoundingBox(yaml.getInt("xmn"), yaml.getInt("ymn"), yaml.getInt("zmn"), yaml.getInt("xmx"), yaml.getInt("ymx"), yaml.getInt("zmx"));
        return new SiteImpl(type, bounds);
    }
}
