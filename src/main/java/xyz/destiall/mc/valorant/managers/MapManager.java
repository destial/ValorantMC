package xyz.destiall.mc.valorant.managers;

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
    private final File mapFolder;
    private static MapManager instance;

    public static MapManager getInstance() {
        return instance;
    }
    public MapManager() {
        instance = this;
        mapFolder = new File(Valorant.getInstance().getPlugin().getDataFolder(), "maps" + File.separator);
        loadMaps();
    }

    public File getMapFolder() {
        return mapFolder;
    }

    public void loadMaps() {
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
        }
        String[] list = mapFolder.list();
        if (list == null) return;
        for (String mapFileName : list) {
            if (!mapFileName.toLowerCase().endsWith(".yml") || !mapFileName.toLowerCase().endsWith(".yaml")) continue;
            Map map = MatchFactory.createMap(YamlConfiguration.loadConfiguration(new File(mapFolder, mapFileName)));
            if (map == null) continue;
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
