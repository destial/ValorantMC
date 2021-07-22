package xyz.destiall.mc.valorant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class MatchCommand extends SubCommand {
    public MatchCommand() {
        super("match");
        tab.add("start");
        tab.add("stop");
        tab.add("new");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        switch (args[0].toLowerCase()) {
            case "start": {
                if (args.length > 2) {
                    try {
                        int id = Integer.parseInt(args[1]);
                        Match match = MatchManager.getInstance().getMatch(id);
                        if (!match.start()) {
                            player.sendMessage(ChatColor.RED + "Not enough players to start the match!");
                        }
                    } catch (NumberFormatException ignored) {}
                }
                break;
            }
            case "stop": {
                if (args.length > 2) {
                    try {
                        int id = Integer.parseInt(args[1]);
                        Match match = MatchManager.getInstance().getMatch(id);
                        match.end(MatchTerminateEvent.Reason.FORCE);
                    } catch (NumberFormatException ignored) {}
                }
                break;
            }
            case "new": {
                // Match match = MatchManager.getInstance().createNewMatch();
                break;
            }
            default: break;
        }
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
