package xyz.destiall.mc.valorant.commands.map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.map.Site;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.commands.SubCommand;
import xyz.destiall.mc.valorant.managers.MapManager;

public class LoadCommand extends SubCommand {
    public LoadCommand() {
        super("load");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        CreationSession session = CreationSession.getSession(player);
        if (session != null) {
            player.sendMessage(ChatColor.RED + "You are already in a creation session! To save your work, enter /valorant map finish");
            player.sendMessage(ChatColor.RED + "To cancel the session, enter /valorant map cancel");
            return;
        }
        if (args.length > 0) {
            String mapname = args[0];
            Map map = MapManager.getInstance().getMaps().stream().filter(m -> m.getName().equalsIgnoreCase(mapname)).findFirst().orElse(null);
            if (map == null) {
                player.sendMessage(ChatColor.RED + "Invalid map name of: " + mapname);
                return;
            }
            session = new CreationSession(player, map.getName(), map.getBounds(), map.getWorld());
            session.setDefenderSpawn(map.getDefenderSpawn());
            session.setAttackerSpawn(map.getAttackerSpawn());
            for (BoundingBox wall : map.getWalls()) {
                session.addWall(wall);
            }
            for (Site site : map.getSites()) {
                session.addSite(site);
            }
            player.sendMessage(ChatColor.GREEN + "Loaded map " + map.getName());
            player.teleport(map.getAttackerSpawn());
            return;
        }
        player.sendMessage(ChatColor.RED + "Usage is: /valorant map load [map]");
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {

    }
}
