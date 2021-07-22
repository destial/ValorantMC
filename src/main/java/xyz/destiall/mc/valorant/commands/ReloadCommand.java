package xyz.destiall.mc.valorant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.Valorant;

public class ReloadCommand extends SubCommand {
    public ReloadCommand() {
        super("reload");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        Valorant.getInstance().disable();
        Valorant.getInstance().enable();
        player.sendMessage(ChatColor.GREEN + "Reloaded ValorantMC");
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        Valorant.getInstance().disable();
        Valorant.getInstance().enable();
        sender.sendMessage("Reloaded ValorantMC");
    }
}
