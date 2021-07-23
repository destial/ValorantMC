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
        if (!config.getString("config-version", "").equalsIgnoreCase(Valorant.VERSION)) {
            createConfig();
            save();
        }
    }

    public void createConfig() {
        config.set("config-version", Valorant.VERSION);
        config.set("database.type", config.getString("database.type") == null ? "sqlite" : config.getString("database.type"));
        config.set("database.mysql.address", config.getString("database.mysql.address") == null ? "localhost" : config.getString("database.mysql.address"));
        config.set("database.mysql.username", config.getString("database.mysql.username") == null ? "root" : config.getString("database.mysql.username"));
        config.set("database.mysql.password", config.getString("database.mysql.password") == null ? "password" : config.getString("database.mysql.password"));
        config.set("database.mysql.database", config.getString("database.mysql.database") == null ? "database" : config.getString("database.mysql.database"));
        config.set("database.mysql.port", config.getString("database.mysql.port") == null ? "3306" : config.getString("database.mysql.port"));
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
