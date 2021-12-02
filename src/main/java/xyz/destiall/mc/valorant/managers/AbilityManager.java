package xyz.destiall.mc.valorant.managers;

import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.Collection;

public class AbilityManager {
    public static void stop(Match match) {
        Collection<VPlayer> list = match.getPlayers().values();
        for (VPlayer p : list) {
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
