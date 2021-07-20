package xyz.destiall.mc.valorant.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class MatchCommand extends SubCommand {
    public MatchCommand() {
        super("match");
        tab.add("start");
        tab.add("stop");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        switch (args[0].toLowerCase()) {
            case "start": {
                //Match match = MatchManager.getInstance().createNewMatch();
                break;
            }
            case "stop": {
                if (args.length > 2) {
                    try {
                        int id = Integer.parseInt(args[2]);
                        Match match = MatchManager.getInstance().getMatch(id);
                    } catch (NumberFormatException ignored) {}
                }
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
