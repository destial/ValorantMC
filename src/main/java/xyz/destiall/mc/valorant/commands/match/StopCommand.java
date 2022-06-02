package xyz.destiall.mc.valorant.commands.match;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.commands.SubCommand;

public class StopCommand extends SubCommand {
    public StopCommand() {
        super("stop");
        permission = "valorant.match.stop";
    }

    @Override
    public void runPlayer(Player player, String[] args) {

    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {

    }
}
