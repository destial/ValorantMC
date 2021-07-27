package xyz.destiall.mc.valorant.commands.match;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.commands.SubCommand;
import xyz.destiall.mc.valorant.commands.ValorantCommand;

import java.util.Arrays;

public class MatchCommand extends SubCommand {
    public MatchCommand() {
        super("match");
        permission = "valorant.admin";
        subCommands.add(new StartCommand());
        subCommands.add(new StopCommand());
        subCommands.add(new NewCommand());
        tab.add("start");
        tab.add("stop");
        tab.add("new");
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
