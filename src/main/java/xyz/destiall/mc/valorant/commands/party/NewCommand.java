package xyz.destiall.mc.valorant.commands.party;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.commands.SubCommand;

public class NewCommand extends SubCommand {
    public NewCommand() {
        super("new");
    }

    @Override
    public void runPlayer(Player player, String[] args) {

    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
