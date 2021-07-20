package xyz.destiall.mc.valorant.commands.map;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.commands.SubCommand;

public class CreateMapCommand extends SubCommand {
    public CreateMapCommand() {
        super("create");
        tab.add("create");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        if (args.length == 0) return;
        String name = String.join(" ", args);
        try {
            Region region = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection(BukkitAdapter.adapt(player.getWorld()));
            BlockVector3 max = region.getMaximumPoint();
            BlockVector3 min = region.getMinimumPoint();
            BoundingBox boundingBox = new BoundingBox(max.getX(), max.getY(), max.getZ(), min.getX(), min.getY(), min.getZ());
            new CreationSession(player, name, boundingBox, player.getWorld());
        } catch (IncompleteRegionException e) {
            player.sendMessage("You have not selected a region!");
        }
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
