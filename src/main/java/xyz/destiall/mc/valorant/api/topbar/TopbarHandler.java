package xyz.destiall.mc.valorant.api.topbar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.destiall.mc.valorant.api.events.countdown.CountdownStartEvent;
import xyz.destiall.mc.valorant.api.events.countdown.CountdownStopEvent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Countdown;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Module;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.ArrayList;
import java.util.Collection;

public class TopbarHandler implements Module, Listener {
    private final Collection<Topbar> bars;
    private final Match match;

    public TopbarHandler(Match match) {
        this.match = match;
        this.bars = new ArrayList<>();
        create();
    }

    @EventHandler
    public void onCountdownStart(CountdownStartEvent e) {
        create();
        e.getCountdown().addEvent(timer -> {
            for (Topbar bar : bars) {
                bar.setTimer(timer, e.getContext().getTime());
            }
        });
    }

    @EventHandler
    public void onCountdownStop(CountdownStopEvent e) {
        destroy();
    }

    public void leave(VPlayer player) {
        bars.stream().filter(b -> b.getTeam() == player.getTeam()).findFirst().get().removePlayer(player.getPlayer());
    }

    public void rejoin(VPlayer player) {
        bars.stream().filter(b -> b.getTeam() == player.getTeam()).findFirst().get().addPlayer(player.getPlayer());
    }

    public void create() {
        if (!bars.isEmpty()) return;
        Countdown countdown = match.getModule(Countdown.class);
        for (Team team : match.getTeams()) {
            Topbar bar = new Topbar(team, countdown);
            bars.add(bar);
        }
    }

    @Override
    public void destroy() {
        for (Topbar bar : bars) {
            bar.destroy();
        }
        bars.clear();
    }
}
