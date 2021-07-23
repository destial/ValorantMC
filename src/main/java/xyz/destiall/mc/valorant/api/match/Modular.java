package xyz.destiall.mc.valorant.api.match;

import java.util.Collection;

public interface Modular<M extends Module> {
    Collection<M> getModules();
    <N extends M> N getModule(Class<? extends N> key);
    default <N extends M> boolean hasModule(Class<? extends N> key) {
        return getModule(key) != null;
    }
}
