package xyz.destiall.mc.valorant;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new Valorant(this);
        Valorant.getInstance().enable();
    }

    @Override
    public void onDisable() {
        Valorant.getInstance().disable();
    }
}
