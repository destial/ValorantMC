package xyz.destiall.mc.valorant;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.utils.ParticleException;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.destiall.mc.valorant.commands.ValorantCommand;
import xyz.destiall.mc.valorant.listeners.InventoryListener;
import xyz.destiall.mc.valorant.listeners.MatchListener;
import xyz.destiall.mc.valorant.listeners.SovaListener;
import xyz.destiall.mc.valorant.listeners.TestListener;
import xyz.destiall.mc.valorant.managers.MapManager;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.io.File;

public class Valorant {
    private static Valorant instance;
    private final JavaPlugin plugin;
    public Valorant(JavaPlugin plugin) {
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
        MapManager.getInstance().unloadMaps();
        Effects.disable();
        HandlerList.unregisterAll(plugin);
        Scheduler.cancelAll();
        plugin.getServer().getPluginCommand("valorant").setExecutor(null);
    }

    public void enable() {
        plugin.saveDefaultConfig();
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        if (!shopFile.exists()) {
            plugin.saveResource("shop.yml", true);
        }
        new MapManager();
        new MatchManager();
        try {
            ParticleNativeAPI api = ParticleNativeCore.loadAPI(plugin);
            new Effects(api);
        } catch (ParticleException e) {
            e.printStackTrace();
        }
        registerListeners();
        registerCommands();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new TestListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new SovaListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new MatchListener(), plugin);
    }

    private void registerCommands() {
        plugin.getServer().getPluginCommand("valorant").setExecutor(new ValorantCommand());
    }
}
