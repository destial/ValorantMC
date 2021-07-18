package xyz.destiall.mc.valorant.managers;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.destiall.mc.valorant.Valorant;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private static ConfigManager instance;
    private YamlConfiguration config;
    private File configFile;
    private ConfigManager() {}

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
            instance.loadConfig();
        }
        return instance;
    }

    public void loadConfig() {
        configFile = new File(Valorant.getInstance().getPlugin().getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                config = YamlConfiguration.loadConfiguration(configFile);
                createConfig();
                save();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void createConfig() {
        config.addDefault("config-version", Valorant.VERSION);
    }

    public Configuration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
