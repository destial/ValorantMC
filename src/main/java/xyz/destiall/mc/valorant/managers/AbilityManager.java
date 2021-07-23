package xyz.destiall.mc.valorant.managers;

import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.match.Match;

import java.util.HashMap;
import java.util.Set;

public class AbilityManager {

    // TODO: Figure out ability manager to stop all abilites regardless of match

    public static final HashMap<Match, Set<Ability>> ALL_ABILITIES = new HashMap<>();
    public static void stop(Match match) {
        ALL_ABILITIES.entrySet().stream().filter(e -> e.getKey() == match).forEach(e -> {
            e.getValue().forEach(Ability::remove);
        });
    }

    public static void stopAll() {
        for (Set<Ability> abilities : ALL_ABILITIES.values()) {
            for (Ability ability : abilities) {
                ability.remove();
            }
            abilities.clear();
        }
        ALL_ABILITIES.clear();
    }
}
