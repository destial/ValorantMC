package xyz.destiall.mc.valorant.commands.map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.commands.SubCommand;
import xyz.destiall.mc.valorant.managers.MapManager;

public class ListCommand extends SubCommand {
    public ListCommand() {
        super("list");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        player.sendMessage(ChatColor.GOLD + "List of maps:");
        for (Map map : MapManager.getInstance().getMaps()) {
            player.sendMessage(ChatColor.YELLOW + " - " + map.getName());
        }
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "List of maps:");
        for (Map map : MapManager.getInstance().getMaps()) {
            sender.sendMessage(ChatColor.YELLOW + " - " + map.getName());
        }
    }
}
