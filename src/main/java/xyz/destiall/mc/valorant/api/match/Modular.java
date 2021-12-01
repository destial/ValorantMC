package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import xyz.destiall.mc.valorant.Valorant;

import java.util.Collection;

public interface Modular {
    Collection<Module> getModules();
    default <N extends Module> N getModule(Class<N> key) {
        return (N) getModules().stream().filter(m -> m.getClass().isAssignableFrom(key)).findFirst().orElse(null);
    }
    default void addModule(Module module) {
        if (hasModule(module.getClass())) return;
        if (module instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) module, Valorant.getInstance().getPlugin());
        }
        getModules().add(module);
    }

    default <N extends Module> void removeModule(Class<N> key) {
        if (!hasModule(key)) return;
        N module = getModule(key);
        removeModule(module);
    }

    default void removeModule(Module module) {
        if (!hasModule(module.getClass())) return;
        module.destroy();
        if (module instanceof Listener) {
            HandlerList.unregisterAll((Listener) module);
        }
        getModules().remove(module);
    }

    default <N extends Module> boolean hasModule(Class<N> key) {
        return getModule(key) != null;
    }
}
