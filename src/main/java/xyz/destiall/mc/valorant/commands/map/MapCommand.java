package xyz.destiall.mc.valorant.commands.map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.commands.SubCommand;
import xyz.destiall.mc.valorant.commands.ValorantCommand;

import java.util.Arrays;

public class MapCommand extends SubCommand {
    public MapCommand() {
        super("map");
        subCommands.add(new ListCommand());
        subCommands.add(new CreateCommand());
        subCommands.add(new FinishCommand());
        subCommands.add(new CancelCommand());
        subCommands.add(new AddWallCommand());
        subCommands.add(new AddSiteCommand());
        subCommands.add(new SetAttackerCommand());
        subCommands.add(new SetDefenderCommand());
        tab.add("list");
        tab.add("create");
        tab.add("finish");
        tab.add("cancel");
        tab.add("addwall");
        tab.add("addsite");
        tab.add("setattackers");
        tab.add("setdefenders");
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
        if (args.length == 0) {
            ValorantCommand.sendError(sender);
            return;
        }
        SubCommand cmd = subCommands.stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (cmd == null) {
            ValorantCommand.sendError(sender);
            return;
        }
        cmd.runConsole(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
