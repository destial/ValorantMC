package xyz.destiall.mc.valorant.commands.party;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.commands.SubCommand;

public class PartyCommand extends SubCommand {
    public PartyCommand() {
        super("party");
        tab.add("new");
        tab.add("join");
    }

    @Override
    public void runPlayer(Player player, String[] args) {

    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {

    }
}
