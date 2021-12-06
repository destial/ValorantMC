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
        int i = 1;
        for (VPlayer p : team.getMembers()) {
            final FastBoard board = boards.get(player.getUUID());
            if (board == null) continue;
            for (VPlayer p1 : team.getMembers()) {
                if (p1.isDead()) {
                    board.updateLine(i++, "");
                    continue;
                }
                board.updateLine(0, ChatColor.BLUE + "Alive Members:");
                board.updateLine(i++, p1.getPlayer().getName());
            }
        }
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
