package xyz.destiall.mc.valorant.commands.map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.commands.SubCommand;

import java.util.Arrays;

public class MapCommand extends SubCommand {
    public MapCommand() {
        super("map");
        subCommands.add(new CreateMapCommand());
        tab.add("create");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        SubCommand cmd = subCommands.stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (cmd == null) return;
        cmd.runPlayer(player, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        SubCommand cmd = subCommands.stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (cmd == null) return;
        cmd.runConsole(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
