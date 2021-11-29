package xyz.destiall.mc.valorant.api.match;

import java.util.Collection;

public interface Modular {
    Collection<Module> getModules();
    <N extends Module> N getModule(Class<N> key);
    default void addModule(Module module) {
        if (hasModule(module.getClass())) return;
        getModules().add(module);
    }
    default <N extends Module> boolean hasModule(Class<N> key) {
        return getModule(key) != null;
    }
}
