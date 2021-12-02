package xyz.destiall.mc.valorant.api.sidebar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.ArrayList;
import java.util.List;

public class BukkitSidebar extends Sidebar {
    private final ScoreboardWrapper board;
    private final Scoreboard emptyBoard;
    private final org.bukkit.scoreboard.Team boardTeam;
    public BukkitSidebar(Team team) {
        super(team);
        board = new ScoreboardWrapper("Match");
        emptyBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        boardTeam = board.getScoreboard().registerNewTeam(""+team.hashCode());
        boardTeam.setNameTagVisibility(NameTagVisibility.NEVER);
        boardTeam.setAllowFriendlyFire(false);
    }

    @Override
    public void create() {
        for (VPlayer player : team.getMembers()) {
            player.getPlayer().setScoreboard(board.scoreboard);
            boardTeam.addEntry(player.getPlayer().getName());
        }
    }

    @Override
    public void rejoin(VPlayer player) {
        invalidate(player);
        player.getPlayer().setScoreboard(board.scoreboard);
        boardTeam.addEntry(player.getPlayer().getName());
    }

    @Override
    public void invalidate(VPlayer player) {
        player.getPlayer().setScoreboard(emptyBoard);
        boardTeam.removeEntry(player.getPlayer().getName());
    }

    @Override
    public void destroy() {
        boardTeam.unregister();
    }

    @Override
    public void render() {
        int i = 0;
        board.setLine(i, ChatColor.BLUE + "Alive Members:");
        for (VPlayer p1 : team.getMembers()) {
            i++;
            if (p1.isDead()) {
                board.setLine(i, "");
                continue;
            }
            board.setLine(i, ChatColor.GREEN + p1.getPlayer().getName());
        }
        board.setLine(++i, " ");
        board.setLine(++i, ChatColor.BLUE + "Your points: " + team.getScore());
        board.setLine(++i, ChatColor.RED + "Enemy points: " + team.getMatch().getOtherTeam(team).getScore());
    }

    public static class ScoreboardWrapper {
        public static final int MAX_LINES = 16;
        private final Scoreboard scoreboard;
        private final Objective objective;
        private final List<String> modifies = new ArrayList<>(MAX_LINES);

        public ScoreboardWrapper(String title) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            objective = scoreboard.registerNewObjective(title, "dummy");
            objective.setDisplayName(title);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        public void setTitle(String title) {
            objective.setDisplayName(title);
        }

        private String getLineCoded(String line) {
            String result = line;
            while (modifies.contains(result)) result += ChatColor.RESET;
            return result.substring(0, Math.min(40, result.length()));
        }

        public void addLine(String line) {
            if (modifies.size() > MAX_LINES) throw new IndexOutOfBoundsException("You cannot add more than 16 lines.");
            String modified = getLineCoded(line);
            modifies.add(modified);
            objective.getScore(modified).setScore(-(modifies.size() / 2));
        }

        public void setLine(int index, String line) {
            if (index < 0 || index >= MAX_LINES) throw new IndexOutOfBoundsException("The index cannot be negative or higher than 15.");
            while (index+1 > modifies.size()) {
                addLine(" ");
            }
            String oldModified = modifies.get(index);
            scoreboard.resetScores(oldModified);
            String modified = getLineCoded(line);
            modifies.set(index, modified);
            objective.getScore(modified).setScore(-((index / 2) + 1));
        }

        public Scoreboard getScoreboard() {
            return scoreboard;
        }
    }
}
