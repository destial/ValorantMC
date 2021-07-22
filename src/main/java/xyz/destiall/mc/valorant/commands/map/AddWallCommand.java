package xyz.destiall.mc.valorant.commands.map;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.commands.SubCommand;

public class AddWallCommand extends SubCommand {
    public AddWallCommand() {
        super("addwall");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        CreationSession session = CreationSession.getSession(player);
        if (session == null) {
            player.sendMessage(ChatColor.RED + "You are not in a creation session! Create a map first via /valorant map create");
            return;
        }
        try {
            Region region = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection(BukkitAdapter.adapt(player.getWorld()));
            BlockVector3 max = region.getMaximumPoint();
            BlockVector3 min = region.getMinimumPoint();
            BoundingBox boundingBox = new BoundingBox(max.getX(), max.getY(), max.getZ(), min.getX(), min.getY(), min.getZ());
            if (session.addWall(boundingBox)) {
                player.sendMessage(ChatColor.GREEN + "Added wall #" + session.getWalls().size());
            } else {
                player.sendMessage(ChatColor.RED + "This region is overlapping an existing wall!");
            }
        } catch (IncompleteRegionException e) {
            player.sendMessage(ChatColor.RED + "You have not selected a region!");
        }
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
