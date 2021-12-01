package xyz.destiall.mc.valorant.commands.match;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.commands.SubCommand;
import xyz.destiall.mc.valorant.managers.MatchManager;

public class JoinCommand extends SubCommand {
    public JoinCommand() {
        super("join");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        Match match = MatchManager.getInstance().getMatch(player);
        if (match != null) {
            player.sendMessage("You are already in a match!");
            return;
        }
        match = MatchManager.getInstance().getEmptyMatch();
        if (match == null) {
            player.sendMessage("No available matches found!");
            return;
        }
        match.join(player);
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
