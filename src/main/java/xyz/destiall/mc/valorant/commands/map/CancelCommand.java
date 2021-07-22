package xyz.destiall.mc.valorant.commands.map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.commands.SubCommand;

public class CancelCommand extends SubCommand {
    public CancelCommand() {
        super("cancel");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        CreationSession session = CreationSession.getSession(player);
        if (session == null) {
            player.sendMessage(ChatColor.RED + "You are not in a creation session! Create a map first via /valorant map create");
            return;
        }
        CreationSession.ACTIVE_SESSIONS.remove(session);
        player.sendMessage(ChatColor.GREEN + "Successfully cancelled your current session!");
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
