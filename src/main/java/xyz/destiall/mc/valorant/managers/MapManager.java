package xyz.destiall.mc.valorant.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.factories.MatchFactory;
import xyz.destiall.mc.valorant.utils.Debugger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private final List<Map> maps = new ArrayList<>();
    private static MapManager instance;

    public static MapManager getInstance() {
        return instance;
    }
    public MapManager() {
        instance = this;
        loadMaps();
    }

    public void loadMaps() {
        File mapFolder = new File(Valorant.getInstance().getPlugin().getDataFolder(), "maps" + File.separator);
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
        }
        if (mapFolder.list() == null) return;
        for (String mapFileName : mapFolder.list()) {
            if (!mapFileName.toLowerCase().endsWith(".yml")) continue;
            File mapFile = new File(mapFolder, mapFileName);
            FileConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            Map map = MatchFactory.createMap(mapConfig);
            Debugger.debug("Loaded Valorant Map " + map.getName());
            maps.add(map);
        }
    }

    public Map getRandomMap() {
        for (Map map : maps) {
            if (map.isInUse()) continue;
            map.setUse(true);
            return map;
        }
        return null;
    }

    public void unloadMaps() {
        maps.clear();
    }
}
