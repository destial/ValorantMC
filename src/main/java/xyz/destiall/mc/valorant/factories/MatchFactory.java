package xyz.destiall.mc.valorant.factories;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.map.Site;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.classes.MapImpl;
import xyz.destiall.mc.valorant.classes.MatchImpl;
import xyz.destiall.mc.valorant.classes.SiteImpl;
import xyz.destiall.mc.valorant.managers.MapManager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MatchFactory {
    private static int MATCHES = 0;
    public static Match createMatch(Map map) {
        return new MatchImpl(map, ++MATCHES);
    }

    public static Map createMap(FileConfiguration yaml) {
        String worldName = yaml.getString("world");
        if (worldName == null) return null;
        String mapName = yaml.getString("name", "A Map");
        World world = Bukkit.getWorld(worldName);
        BoundingBox bounds = getXYZBounds(yaml);
        float spawnRadius = (float) yaml.getDouble("spawn.radius");
        Location attacker = new Location(world, yaml.getDouble("spawn.attacker.x"), yaml.getDouble("spawn.attacker.y"), yaml.getDouble("spawn.attacker.z"), (float) yaml.getDouble("spawn.attacker.yaw"), (float) yaml.getDouble("spawn.attacker.pitch"));
        Location defender = new Location(world, yaml.getDouble("spawn.defender.x"), yaml.getDouble("spawn.defender.y"), yaml.getDouble("spawn.defender.z"), (float) yaml.getDouble("spawn.defender.yaw"), (float) yaml.getDouble("spawn.defender.pitch"));
        Set<Site> sites = new HashSet<>();
        ConfigurationSection siteSection = yaml.getConfigurationSection("sites");
        if (siteSection == null) return null;
        for (String key : siteSection.getKeys(false)) {
            ConfigurationSection section = yaml.getConfigurationSection("sites." + key);
            if (section == null) continue;
            if (key.equalsIgnoreCase("A")) {
                sites.add(createSite(Site.Type.A, section));
            } else if (key.equalsIgnoreCase("B")) {
                sites.add(createSite(Site.Type.B, section));
            } else if (key.equalsIgnoreCase("C")) {
                sites.add(createSite(Site.Type.C, section));
            }
        }
        Set<BoundingBox> walls = new HashSet<>();
        ConfigurationSection wallSection = yaml.getConfigurationSection("walls");
        if (wallSection == null) return null;
        for (String key : wallSection.getKeys(false)) {
            ConfigurationSection section = yaml.getConfigurationSection("walls." + key);
            if (section == null) continue;
            walls.add(getXYZBounds(section));
        }
        return new MapImpl(mapName.trim(), world, bounds, spawnRadius, attacker, defender, sites, walls);
    }

    public static Map createMap(CreationSession session) {
        YamlConfiguration config = new YamlConfiguration();
        String fileName = session.getMapName() + ".yml";
        fileName = fileName.replace(" ", "-");
        File file = new File(MapManager.getInstance().getMapFolder(), fileName);
        if (file.exists()) return createMap(YamlConfiguration.loadConfiguration(file));
        try {
            config.set("name", session.getMapName());
            config.set("world", session.getWorld().getName());
            setBounds("bounds", config, session.getBounds());
            config.set("spawn.radius", 3);
            if (session.getAttackerSpawn() == null) return null;
            setLocation("spawn.attacker", config, session.getAttackerSpawn());
            if (session.getDefenderSpawn() == null) return null;
            setLocation("spawn.defender", config, session.getDefenderSpawn());
            if (session.getSites().size() == 0) return null;
            for (Site site : session.getSites()) {
                setBounds("sites." + site.getSiteType().name(), config, site.getBounds());
            }
            if (session.getWalls().size() == 0) return null;
            int i = 1;
            for (BoundingBox wall : session.getWalls()) {
                setBounds("walls." + i, config, wall);
                ++i;
            }
            config.save(file);
            CreationSession.ACTIVE_SESSIONS.remove(session);
            return createMap(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setBounds(String path, Configuration config, BoundingBox bounds) {
        config.set(path + ".xmx", bounds.getMaxX());
        config.set(path + ".ymx", bounds.getMaxY());
        config.set(path + ".zmx", bounds.getMaxZ());
        config.set(path + ".xmn", bounds.getMinX());
        config.set(path + ".ymn", bounds.getMinY());
        config.set(path + ".zmn", bounds.getMinZ());
    }

    private static void setLocation(String path, Configuration config, Location location) {
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
    }

    public static Site createSite(Site.Type type, ConfigurationSection yaml) {
        return new SiteImpl(type, getXYZBounds(yaml));
    }

    private static BoundingBox getXYZBounds(ConfigurationSection yaml) {
        return new BoundingBox(yaml.getInt("xmn"), yaml.getInt("ymn"), yaml.getInt("zmn"), yaml.getInt("xmx"), yaml.getInt("ymx"), yaml.getInt("zmx"));
    }
}
