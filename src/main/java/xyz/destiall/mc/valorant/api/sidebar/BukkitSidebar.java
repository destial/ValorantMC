package xyz.destiall.mc.valorant.api.sidebar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BukkitSidebar extends Sidebar {
    private final Scoreboard emptyBoard;
    private final Map<UUID, ScoreboardWrapper> boards;

    public BukkitSidebar(Team team) {
        super(team);
        emptyBoard = Bukkit.getScoreboardManager().getMainScoreboard();
        boards = new HashMap<>();
    }

    @Override
    public void create() {
        Team otherTeam = team.getMatch().getOtherTeam(team);
        for (VPlayer player : team.getMembers()) {
            ScoreboardWrapper board = new ScoreboardWrapper("Match");
            org.bukkit.scoreboard.Team boardTeam = board.getScoreboard().registerNewTeam("" + team.hashCode());
            boardTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
            boardTeam.setAllowFriendlyFire(false);
            boardTeam.setColor(ChatColor.AQUA);
            boardTeam.addEntry(player.getPlayer().getName());
            boards.put(player.getUUID(), board);

            for (VPlayer player1 : team.getMembers()) {
                if (player == player1) continue;
                boardTeam.addEntry(player1.getPlayer().getName());
            }

            org.bukkit.scoreboard.Team otherBoardTeam = board.getScoreboard().registerNewTeam("" + otherTeam.hashCode());
            otherBoardTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
            for (VPlayer enemy : otherTeam.getMembers()) {
                otherBoardTeam.addEntry(enemy.getPlayer().getName());
            }

            player.getPlayer().setScoreboard(board.scoreboard);
        }
    }

    @Override
    public void rejoin(VPlayer player) {
        ScoreboardWrapper board = boards.get(player.getUUID());
        player.getPlayer().setScoreboard(board.scoreboard);
    }

    @Override
    public void invalidate(VPlayer player) {
        player.getPlayer().setScoreboard(emptyBoard);
    }

    @Override
    public void destroy() {
        for (VPlayer player : team.getMembers()) {
            player.getPlayer().setScoreboard(emptyBoard);
        }
        for (ScoreboardWrapper board : boards.values()) {
            board.objective.unregister();
            board.scoreboard.getTeam("" + team.hashCode()).unregister();
        }
        boards.clear();
    }

    @Override
    public void render(VPlayer player) {
        ScoreboardWrapper board = boards.get(player.getUUID());
        if (board == null) return;
        board.setLine(0, ChatColor.AQUA + "Alive Members:");
        int i = -1;
        for (VPlayer p1 : team.getMembers()) {
            if (p1.isDead()) {
                board.setLine(++i, " ");
                continue;
            }
            board.setLine(++i, ChatColor.GREEN + p1.getPlayer().getName());
        }
        board.setLine(++i, " ");
        board.setLine(++i, ChatColor.GREEN + "Money: $" + player.getEconomy().getBalance());

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
