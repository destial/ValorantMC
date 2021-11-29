package xyz.destiall.mc.valorant.commands.party;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.commands.SubCommand;
import xyz.destiall.mc.valorant.commands.ValorantCommand;

import java.util.Arrays;

public class PartyCommand extends SubCommand {
    public PartyCommand() {
        super("party");
        subCommands.add(new NewCommand());
        subCommands.add(new JoinCommand());
        tab.add("new");
        tab.add("join");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        if (args.length == 0) {
            ValorantCommand.sendError(player);
            return;
        }
        SubCommand cmd = subCommands.stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (cmd == null) {
            ValorantCommand.sendError(player);
            return;
        }
        cmd.runPlayer(player, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
