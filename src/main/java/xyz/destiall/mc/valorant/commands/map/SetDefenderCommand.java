package xyz.destiall.mc.valorant.commands.map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.session.CreationSession;
import xyz.destiall.mc.valorant.commands.SubCommand;

public class SetDefenderCommand extends SubCommand {
    public SetDefenderCommand() {
        super("setdefenders");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        CreationSession session = CreationSession.getSession(player);
        if (session == null) {
            player.sendMessage(ChatColor.RED + "You are not in a creation session! Create a map first via /valorant map create");
            return;
        }
        Location loc = player.getLocation().clone();
        session.setDefenderSpawn(loc);
        player.sendMessage(ChatColor.GREEN + "Set current location as defender spawn!");
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
