package xyz.destiall.mc.valorant.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.agents.jett.BladeStorm;
import xyz.destiall.mc.valorant.agents.jett.CloudBurst;
import xyz.destiall.mc.valorant.agents.jett.Updraft;
import xyz.destiall.mc.valorant.agents.reyna.Leer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValorantCommand implements CommandExecutor, TabExecutor {
    private final Valorant valorant;
    public ValorantCommand() {
        valorant = Valorant.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) return false;
            Player player = (Player) sender;
            switch (args[0].toLowerCase()) {
                case "cloudburst": {
                    CloudBurst cb = new CloudBurst();
                    cb.use(player, player.getLocation().getDirection());
                    break;
                }
                case "leer": {
                    Leer leer = new Leer();
                    leer.use(player, player.getLocation().getDirection());
                    break;
                }
                case "updraft": {
                    Updraft ud = new Updraft();
                    ud.use(player, player.getLocation().getDirection());
                    break;
                }
                case "bladestorm": {
                    BladeStorm bs = new BladeStorm();
                    bs.use(player, player.getLocation().getDirection());
                    break;
                }
                default: break;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return new ArrayList<>();
        List<String> tab = Arrays.asList("cloudburst", "updraft", "leer");
        return tab.stream().filter(a -> a.toLowerCase().contains(args[0].toLowerCase())).collect(Collectors.toList());
    }
}
