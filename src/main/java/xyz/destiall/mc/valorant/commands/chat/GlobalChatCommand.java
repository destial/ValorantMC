package xyz.destiall.mc.valorant.commands.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.player.Participant;
import xyz.destiall.mc.valorant.api.player.Settings;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class GlobalChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        Participant participant = MatchManager.getInstance().getParticipant(player);
        if (participant == null) return false;
        participant.setChatSettings(Settings.Chat.GLOBAL);
        participant.sendMessage(ChatColor.BLUE + "Set chat messages to global");
        return false;
    }
}
