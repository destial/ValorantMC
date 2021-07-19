package xyz.destiall.mc.valorant.managers;

import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.abilities.Ability;

import java.util.HashMap;

public class AbilityManager {
    public static final HashMap<Match, Ability> ALL_ABILITIES = new HashMap<>();
    public static void stop(Match match) {
        ALL_ABILITIES.entrySet().stream().filter(e -> e.getKey().equals(match)).map(e -> {
            e.getValue().remove();
            return null;
        });
    }

    public static void stopAll() {
        for (Ability ability : ALL_ABILITIES.values()) {
            ability.remove();
        }
        ALL_ABILITIES.clear();
    }
}
