package xyz.destiall.mc.valorant;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.destiall.mc.valorant.commands.ValorantCommand;
import xyz.destiall.mc.valorant.commands.chat.GlobalChatCommand;
import xyz.destiall.mc.valorant.commands.chat.TeamChatCommand;
import xyz.destiall.mc.valorant.listeners.ChatListener;
import xyz.destiall.mc.valorant.listeners.GunListener;
import xyz.destiall.mc.valorant.listeners.InventoryListener;
import xyz.destiall.mc.valorant.listeners.MatchListener;
import xyz.destiall.mc.valorant.listeners.SovaListener;
import xyz.destiall.mc.valorant.listeners.TestListener;
import xyz.destiall.mc.valorant.managers.AbilityManager;
import xyz.destiall.mc.valorant.managers.ConfigManager;
import xyz.destiall.mc.valorant.managers.MapManager;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Debugger;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.io.File;

public final class Valorant {
    private static Valorant instance;
    private final JavaPlugin plugin;
    public static final String VERSION = "1.1";
    public Valorant(JavaPlugin plugin) {
        if (plugin != null && instance == null) {
            instance = this;
        } else {
            instance = null;
        }
        this.plugin = plugin;
    }

    public static Valorant getInstance() {
        return instance;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void disable() {
        AbilityManager.stopAll();
        Debugger.debug("------ Unloaded AbilityManager ------");
        Effects.disable();
        Debugger.debug("------ Unloaded Effects ------ ");
        MatchManager.getInstance().disable();
        Debugger.debug("------ Unloaded MatchManager ------");
        MapManager.getInstance().unloadMaps();
        Debugger.debug("------ Unloaded MapManager ------");
        HandlerList.unregisterAll(plugin);
        ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);
        Debugger.debug("------ Unregistered Listeners ------");
        Scheduler.cancelAll();
        Bukkit.getScheduler().cancelTasks(plugin);
        Debugger.debug("------ Cancelled all tasks ------");
        PluginCommand command = plugin.getServer().getPluginCommand("valorant");
        if (command != null) {
            command.setExecutor(null);
        }
        Debugger.debug("------ Unregistered Commands ------");
    }

    public void enable() {
        ConfigManager.getInstance();
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        if (!shopFile.exists()) {
            plugin.saveResource("shop.yml", true);
        }
        MapManager.getInstance();
        Debugger.debug("------ Loaded MapManager ------");
        MatchManager.getInstance();
        Debugger.debug("------ Loaded MatchManager ------");
        try {
            new Effects(ParticleNativeCore.loadAPI(plugin));
            Debugger.debug("------ Loaded Effects ------");
        } catch (Exception e) {
            e.printStackTrace();
            Debugger.warn("------ Error while loading effects ------");
            Debugger.warn("------ Disabling ValorantMC ------");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }
        registerListeners();
        Debugger.debug("------ Loaded Listeners ------");
        registerCommands();
        Debugger.debug("------ Loaded Commands ------");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new TestListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new SovaListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new MatchListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GunListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), plugin);
    }

    private void registerCommands() {
        PluginCommand command = plugin.getServer().getPluginCommand("valorant");
        if (command == null) {
            Debugger.warn("------ Error while loading commands ------");
            Debugger.warn("------ Disabling ValorantMC ------");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }
        command.setExecutor(new ValorantCommand());

        command = plugin.getServer().getPluginCommand("globalchat");
        if (command == null) {
            Debugger.warn("------ Error while loading commands ------");
            Debugger.warn("------ Disabling ValorantMC ------");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }
        command.setExecutor(new GlobalChatCommand());

        command = plugin.getServer().getPluginCommand("teamchat");
        if (command == null) {
            Debugger.warn("------ Error while loading commands ------");
            Debugger.warn("------ Disabling ValorantMC ------");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }
        command.setExecutor(new TeamChatCommand());
    }
}
