package xyz.destiall.mc.valorant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.commands.map.MapCommand;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValorantCommand implements CommandExecutor, TabExecutor {
    private final Set<SubCommand> commands = new HashSet<>();
    public ValorantCommand() {
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
        SubCommand cmd = commands.stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (cmd == null) return new LinkedList<>();
        SubCommand cmd2 = cmd.getSubCommands().stream().filter(c -> c.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);
        if (cmd2 != null) {
            return cmd2.getTab().stream().filter(c -> c.toLowerCase().contains(args[args.length - 1])).collect(Collectors.toList());
        }
        return cmd.getTab().stream().filter(c -> c.toLowerCase().contains(args[args.length - 1])).collect(Collectors.toList());
    }

    public static void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command is: /valorant");
    }

    public static void sendError(CommandSender sender) {
        sender.sendMessage("Error while using command!");
    }
}
