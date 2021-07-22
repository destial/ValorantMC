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
import xyz.destiall.mc.valorant.api.map.Site;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.classes.SiteImpl;
import xyz.destiall.mc.valorant.commands.SubCommand;

public class AddSiteCommand extends SubCommand {
    public AddSiteCommand() {
        super("addsite");
        tab.add("A");
        tab.add("B");
        tab.add("C");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        CreationSession session = CreationSession.getSession(player);
        if (session == null) {
            player.sendMessage(ChatColor.RED + "You are not in a creation session! Create a map first via /valorant map create");
            return;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Please enter the site name! (A, B, C)");
            return;
        }
        String siteType = args[0].toUpperCase();
        Site.Type type = null;
        switch (siteType) {
            case "A":
                type = Site.Type.A;
                break;
            case "B":
                type = Site.Type.B;
                break;
            case "C":
                type = Site.Type.C;
                break;
            default: break;
        }
        if (type == null) {
            player.sendMessage(ChatColor.RED + "Please enter the site name! (A, B, C)");
            return;
        }
        try {
            Region region = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection(BukkitAdapter.adapt(player.getWorld()));
            BlockVector3 max = region.getMaximumPoint();
            BlockVector3 min = region.getMinimumPoint();
            BoundingBox boundingBox = new BoundingBox(max.getX(), max.getY(), max.getZ(), min.getX(), min.getY(), min.getZ());
            Site site = new SiteImpl(type, boundingBox);
            if (session.addSite(site)) {
                player.sendMessage(ChatColor.GREEN + "Added site " + site.getSiteType().name());
            } else {
                player.sendMessage(ChatColor.RED + "This site has already been added!");
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
