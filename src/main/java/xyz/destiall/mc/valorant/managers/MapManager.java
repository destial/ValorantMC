package xyz.destiall.mc.valorant.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.factories.MapMatchFactory;
import xyz.destiall.mc.valorant.utils.Debugger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class MapManager {
    private final Set<Map> MAPS = new HashSet<>();
    private final File mapFolder;
    private static MapManager instance;

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
            instance.loadMaps();
        }
        return instance;
    }

    private MapManager() {
        instance = this;
        mapFolder = new File(Valorant.getInstance().getPlugin().getDataFolder(), "maps" + File.separator);
    }

    public File getMapFolder() {
        return mapFolder;
    }

    public void loadMaps() {
        if (!mapFolder.exists()) {
            if (mapFolder.mkdir()) {
                Debugger.debug("------ Performing first time setup ------");
                return;
            }
        }
        String[] list = mapFolder.list();
        if (list == null) return;
        for (String mapFileName : list) {
            if (!mapFileName.toLowerCase().endsWith(".yml") && !mapFileName.toLowerCase().endsWith(".yaml")) continue;
            Map map = MapMatchFactory.createMap(YamlConfiguration.loadConfiguration(new File(mapFolder, mapFileName)));
            if (map == null) continue;
            Debugger.debug("------ Loaded Map (" + map.getName() + ") ------");
            MAPS.add(map);
        }
    }

    public Map getRandomMap() {
        for (Map map : MAPS) {
            if (map.isInUse()) continue;
            map.setUse(true);
            return map;
        }
        return null;
    }

    public Set<Map> getMaps() {
        return MAPS;
    }

    public void unloadMaps() {
        MAPS.clear();
    }
}
