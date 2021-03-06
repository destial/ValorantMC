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
import xyz.destiall.mc.valorant.managers.MapManager;

public class CreateCommand extends SubCommand {
    public CreateCommand() {
        super("create");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        try {
            CreationSession session = CreationSession.getSession(player);
            if (session != null) {
                player.sendMessage(ChatColor.RED + "You are already in a creation session! To save your work, enter /valorant map finish");
                player.sendMessage(ChatColor.RED + "To cancel the session, enter /valorant map cancel");
                return;
            }
            Region region = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection(BukkitAdapter.adapt(player.getWorld()));
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "You need to name the map!");
                return;
            }
            String name = String.join(" ", args);
            if (MapManager.getInstance().getMaps().stream().anyMatch(m -> m.getName().equalsIgnoreCase(name))) {
                player.sendMessage(ChatColor.RED + "There is already a map with the same name! Load that map instead with /valorant map load [name]");
                return;
            }
            BlockVector3 max = region.getMaximumPoint();
            BlockVector3 min = region.getMinimumPoint();
            BoundingBox boundingBox = new BoundingBox(max.getX(), max.getY(), max.getZ(), min.getX(), min.getY(), min.getZ());
            new CreationSession(player, name, boundingBox, player.getWorld());
            player.sendMessage(ChatColor.GREEN + "You have created map " + name + "! Continue setting up the map with /valorant map");
        } catch (IncompleteRegionException e) {
            player.sendMessage(ChatColor.RED + "You have not selected a region!");
        }
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
