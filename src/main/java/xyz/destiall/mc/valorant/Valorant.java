package xyz.destiall.mc.valorant;

import org.bukkit.plugin.Plugin;
import xyz.destiall.mc.valorant.managers.MapManager;
import xyz.destiall.mc.valorant.managers.MatchManager;

import java.io.File;

public class Valorant {
    private static Valorant instance;
    private final Plugin plugin;
    public Valorant(Plugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    public static Valorant getInstance() {
        return instance;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void disable() {

    }

    public void enable() {
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        if (!shopFile.exists()) {
            plugin.saveResource("shop.yml", true);
        }
        new MapManager();
        new MatchManager();
    }

    public static MatchManager getMatchManager() {
        return MatchManager.getInstance();
    }
}
