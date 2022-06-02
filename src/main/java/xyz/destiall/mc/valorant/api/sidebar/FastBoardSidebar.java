package xyz.destiall.mc.valorant.api.sidebar;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.player.VPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class FastBoardSidebar extends Sidebar {
    private final HashMap<UUID, FastBoard> boards;
    public FastBoardSidebar(Team team) {
        super(team);
        boards = new HashMap<>();
        create();
    }

    @Override
    public void create() {
        for (VPlayer player : team.getMembers()) {
            FastBoard board = new FastBoard(player.getPlayer());
            boards.put(player.getUUID(), board);
        }
    }

    @Override
    public void render(VPlayer player) {
        FastBoard board = boards.get(player.getUUID());
        if (board == null) return;
        int i = 0;
        board.updateLine(i++, ChatColor.AQUA + "Alive Members:");
        for (VPlayer p1 : team.getMembers()) {
            if (p1.isDead()) {
                board.updateLine(i++, "");
                continue;
            }
            board.updateLine(i++, ChatColor.GREEN + p1.getPlayer().getName());
        }
        board.updateLine(++i, " ");
        board.updateLine(++i, ChatColor.AQUA + "Your points: " + team.getScore());
        board.updateLine(++i, ChatColor.RED + "Enemy points: " + team.getMatch().getOtherTeam(team).getScore());
        board.updateLine(++i, " ");
        board.updateLine(++i, ChatColor.GREEN + "Money: $" + player.getEconomy().getBalance());
    }

    @Override
    public void rejoin(VPlayer player) {
        FastBoard b = boards.get(player.getUUID());
        if (b != null && !b.isDeleted()) {
            b.delete();
            boards.put(player.getUUID(), new FastBoard(player.getPlayer()));
        }
    }

    @Override
    public void invalidate(VPlayer player) {
        FastBoard b = boards.get(player.getUUID());
        if (b != null && !b.isDeleted()) {
            b.delete();
            boards.remove(player.getUUID());
        }
    }

    @Override
    public void destroy() {
        Collection<FastBoard> list = boards.values();
        for (FastBoard fb : list) {
            fb.delete();
        }
        boards.clear();
    }
}
