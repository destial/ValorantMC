package xyz.destiall.mc.valorant.api.topbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Countdown;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Formatter;

import java.time.Duration;
import java.util.List;

public class Topbar {
    private final BossBar bossBar;
    private final Team team;

    public Topbar(Team team, Countdown countdown) {
        this.team = team;
        BarColor color = countdown != null ? countdown.getContext().getColor() : BarColor.BLUE;
        String title = countdown != null ? countdown.getContext().getTitle() : "Playing ValorantMC";
        bossBar = Bukkit.createBossBar(title, color, BarStyle.SOLID);
        if (countdown != null) {
            setTimer(countdown.getRemaining(), countdown.getContext().getTime());
        }
        for (VPlayer player : team.getMembers()) {
            bossBar.addPlayer(player.getPlayer());
        }
        setVisible(true);
    }

    public void setVisible(boolean visible) {
        bossBar.setVisible(visible);
    }

    public void setProgress(float progress) {
        bossBar.setProgress(progress);
    }

    public void setColor(BarColor color) {
        bossBar.setColor(color);
    }

    public void setStyle(BarStyle style) {
        bossBar.setStyle(style);
    }

    public BarColor getColor() {
        return bossBar.getColor();
    }

    public double getProgress() {
        return bossBar.getProgress();
    }

    public void setTimer(Duration timer, float max) {
        String duration = Formatter.duration(timer);
        if (timer.getSeconds() < 60 && timer.getSeconds() > 10) {
            duration = Formatter.durationSeconds(timer);
        }
        String c = "&f";
        double progress = getProgress();
        if (progress < 0.25 && getColor() != BarColor.RED) {
            setColor(BarColor.RED);
        }
        if (progress < 0.25) {
            c = "&c";
        }
        setProgress(timer.getSeconds() / max);
        bossBar.setTitle(Formatter.color("&b" + team.getScore() + "&f | " + c + duration + "&f | &c" + team.getMatch().getOtherTeam(team).getScore()));
    }

    public void addPlayer(Player p) {
        bossBar.addPlayer(p);
    }

    public void removePlayer(Player p) {
        bossBar.removePlayer(p);
    }

    public Team getTeam() {
        return team;
    }

    public void destroy() {
        List<Player> players = bossBar.getPlayers();
        for (Player p : players) {
            bossBar.removePlayer(p);
        }
        setVisible(false);
        bossBar.hide();
        bossBar.removeAll();
    }
}
