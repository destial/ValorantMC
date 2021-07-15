package xyz.destiall.mc.valorant;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import xyz.destiall.mc.valorant.commands.ValorantCommand;
import xyz.destiall.mc.valorant.listeners.HotbarSwapListener;
import xyz.destiall.mc.valorant.listeners.TestListener;
import xyz.destiall.mc.valorant.managers.MapManager;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Effects;

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
        MatchManager.getInstance().disable();
        Effects.disable();
    }

    public void enable() {
        plugin.saveDefaultConfig();
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        if (!shopFile.exists()) {
            plugin.saveResource("shop.yml", true);
        }
        new MapManager();
        new MatchManager();
        new Effects();
        registerListeners();
        registerCommands();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new TestListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new HotbarSwapListener(), plugin);
    }

    private void registerCommands() {
        plugin.getServer().getPluginCommand("valorant").setExecutor(new ValorantCommand());
    }
}
