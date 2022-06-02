package xyz.destiall.mc.valorant.commands.match;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.commands.SubCommand;
import xyz.destiall.mc.valorant.commands.ValorantCommand;

import java.util.Arrays;

public class MatchCommand extends SubCommand {
    public MatchCommand() {
        super("match");
        permission = "valorant.match";
        subCommands.add(new StartCommand());
        subCommands.add(new StopCommand());
        subCommands.add(new NewCommand());
        subCommands.add(new JoinCommand());
        tab.add("start");
        tab.add("stop");
        tab.add("new");
        tab.add("join");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        if (args.length == 0) {
            ValorantCommand.sendCommands(this, player);
            return;
        }
        SubCommand cmd = subCommands.stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (cmd == null) {
            ValorantCommand.sendCommands(this, player);
            return;
        }
        if (cmd.getPermission() == null || player.hasPermission(cmd.getPermission())) {
            cmd.runPlayer(player, Arrays.copyOfRange(args, 1, args.length));
        }
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
