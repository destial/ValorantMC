package xyz.destiall.mc.valorant.commands.map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.commands.SubCommand;

public class FinishCommand extends SubCommand {
    public FinishCommand() {
        super("finish");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        CreationSession session = CreationSession.getSession(player);
        if (session == null) {
            player.sendMessage(ChatColor.RED + "You are not in a creation session! Create a map first via /valorant map create");
            return;
        }
        if (session.finish()) {
            player.sendMessage(ChatColor.GREEN + "Successfully created map " + session.getMapName());
        } else {
            player.sendMessage(ChatColor.RED + "There was an error while creating the map! Have you added the sites, walls and spawn locations?");
        }
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
