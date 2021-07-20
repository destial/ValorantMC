package xyz.destiall.mc.valorant.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.agents.cypher.CyberCage;
import xyz.destiall.mc.valorant.agents.jett.BladeStorm;
import xyz.destiall.mc.valorant.agents.jett.CloudBurst;
import xyz.destiall.mc.valorant.agents.jett.Updraft;
import xyz.destiall.mc.valorant.agents.phoenix.Blaze;
import xyz.destiall.mc.valorant.agents.reyna.Leer;

public class AbilityCommand extends SubCommand {
    public AbilityCommand() {
        super("ability");
        tab.add("bladestorm");
        tab.add("blaze");
        tab.add("cloudburst");
        tab.add("cybercage");
        tab.add("leer");
        tab.add("updraft");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        if (args.length == 0) {
            return;
        }
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
            default: break;
        }
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
