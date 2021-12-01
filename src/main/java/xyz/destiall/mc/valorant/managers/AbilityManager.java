package xyz.destiall.mc.valorant.managers;

import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.player.VPlayer;

public class AbilityManager {
    public static void stop(Match match) {
        for (VPlayer p : match.getPlayers().values()) {
            for (Ability a : p.getAbilities().keySet()) {
                a.remove();
            }
            p.getAbilities().clear();
        }
    }

    public static void stopAll() {
        for (Match match : MatchManager.getInstance().getAllMatches()) {
            stop(match);
        }
    }
}
