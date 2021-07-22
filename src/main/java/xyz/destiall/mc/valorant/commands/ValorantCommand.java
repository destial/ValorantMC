package xyz.destiall.mc.valorant.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.commands.map.MapCommand;

import java.util.*;
import java.util.stream.Collectors;

public class ValorantCommand implements CommandExecutor, TabExecutor {
    private final Valorant valorant;
    private final Set<SubCommand> commands = new HashSet<>();
    public ValorantCommand() {
        valorant = Valorant.getInstance();
        commands.add(new AbilityCommand());
        commands.add(new MatchCommand());
        commands.add(new MapCommand());
        commands.add(new GunCommand());
        commands.add(new ReloadCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return false;
        }
        SubCommand cmd = commands.stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (cmd == null) {
            sendError(sender);
            return false;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            cmd.runPlayer(player, Arrays.copyOfRange(args, 1, args.length));
        } else {
            cmd.runConsole(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args == null || args.length == 0) return new LinkedList<>();
        if (args.length == 1) {
            return commands.stream().filter(c -> c.getName().toLowerCase().contains(args[0])).map(SubCommand::getName).collect(Collectors.toList());
        }
        SubCommand cmd = commands.stream().filter(c -> c.getName().toLowerCase().contains(args[0])).findFirst().orElse(null);
        if (cmd == null) return new LinkedList<>();
        String arg = args[1];
        for (int i = 1; i <= args.length - 1; ++i) {
            arg = args[i];
            final String finalArg = arg;
            cmd = cmd.getSubCommands().stream().filter(c -> c.getName().equalsIgnoreCase(finalArg)).findFirst().orElse(null);
            if (cmd == null) break;
        }
        if (cmd == null) return new LinkedList<>();
        final String finalArg1 = arg;
        return cmd.getTab().stream().filter(c -> c.toLowerCase().contains(finalArg1)).collect(Collectors.toList());
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("Command is: /valorant");
    }

    public void sendError(CommandSender sender) {
        sender.sendMessage("Error while using command!");
    }
}
