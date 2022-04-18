package xyz.destiall.mc.valorant.commands.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.player.Settings;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class TeamChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        VPlayer vPlayer = MatchManager.getInstance().getPlayer(player);
        if (vPlayer == null) return false;
        vPlayer.setChatSettings(Settings.Chat.TEAM);
        vPlayer.sendMessage(ChatColor.BLUE + "Set chat messages to team");
        return false;
    }
}
