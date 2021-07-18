package xyz.destiall.mc.valorant.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.agents.cypher.CyberCage;
import xyz.destiall.mc.valorant.agents.jett.BladeStorm;
import xyz.destiall.mc.valorant.agents.jett.CloudBurst;
import xyz.destiall.mc.valorant.agents.jett.Updraft;
import xyz.destiall.mc.valorant.agents.phoenix.Blaze;
import xyz.destiall.mc.valorant.agents.reyna.Leer;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.managers.MapManager;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
            if (args[0].equalsIgnoreCase("ability") && args.length > 1) {
                switch (args[1].toLowerCase()) {
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
                    case "cybercage": {
                        CyberCage cc = new CyberCage();
                        cc.use(player, player.getLocation().getDirection());
                        break;
                    }
                    case "blaze": {
                        Blaze b = new Blaze();
                        b.use(player, player.getLocation().getDirection());
                        break;
                    }
                    default:
                        break;
                }
            } else if (args[0].equalsIgnoreCase("match") && args.length > 1) {
                switch (args[1].toLowerCase()) {
                    case "start": {
                        //Match match = MatchManager.getInstance().createNewMatch();
                        break;
                    }
                    case "end": {
                        if (args.length > 2) {
                            try {
                                int id = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ignored) {}
                        }
                        break;
                    }
                    default: break;
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                Valorant.getInstance().disable();
                Valorant.getInstance().enable();
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args == null || args.length == 0) return new ArrayList<>();
        if (args.length == 1) {
            List<String> tab = Arrays.asList("match", "ability", "reload");
            return tab.stream().filter(a -> a.toLowerCase().contains(args[0].toLowerCase())).collect(Collectors.toList());
        }
        if (args[0].equalsIgnoreCase("ability")) {
            List<String> tab = Arrays.asList("cloudburst", "updraft", "leer", "bladestorm", "cybercage", "blaze");
            return tab.stream().filter(a -> a.toLowerCase().contains(args[1].toLowerCase())).collect(Collectors.toList());
        }
        if (args[0].equalsIgnoreCase("match")) {
            if (args.length == 2) {
                List<String> tab = Arrays.asList("start", "end");
                return tab.stream().filter(a -> a.toLowerCase().contains(args[1].toLowerCase())).collect(Collectors.toList());
            }
            if (args[1].equalsIgnoreCase("end")) {
                return MatchManager.getInstance().getAllMatches().stream().map(m -> String.valueOf(m.getID())).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
}
