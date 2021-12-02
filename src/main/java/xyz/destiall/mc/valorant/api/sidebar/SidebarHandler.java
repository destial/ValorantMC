package xyz.destiall.mc.valorant.api.sidebar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import xyz.destiall.mc.valorant.api.events.player.DeathEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundFinishEvent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Module;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.Collection;
import java.util.HashSet;

public class SidebarHandler implements Module, Listener {
    private final Collection<Sidebar> sidebars;
    public SidebarHandler(Match match, Class<? extends Sidebar> clazz) {
        sidebars = new HashSet<>();
        try {
            for (Team team : match.getTeams()) {
                Sidebar bar = clazz.getConstructor(Team.class).newInstance(team);
                bar.create();
                sidebars.add(bar);
            }
        } catch (Exception e) {
            for (Team team : match.getTeams()) {
                sidebars.add(new FastBoardSidebar(team));
            }
        }
        render();
    }

    public void rejoin(VPlayer player) {
        for (Sidebar bar : sidebars) {
            bar.rejoin(player);
        }
        render();
    }

    public void render() {
        for (Sidebar bar : sidebars) {
            bar.render();
        }
    }

    @Override
    public void destroy() {
        for (Sidebar bar : sidebars) {
            bar.destroy();
        }
        sidebars.clear();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onDeath(DeathEvent e) {
        render();
    }

    @EventHandler
    public void onRound(RoundFinishEvent e) {
        render();
    }
}
