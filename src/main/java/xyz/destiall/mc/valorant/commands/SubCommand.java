package xyz.destiall.mc.valorant.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SubCommand {
    protected final Set<SubCommand> subCommands = new HashSet<>();
    protected final List<String> tab = new ArrayList<>();
    protected final String name;
    protected String permission;
    public SubCommand(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public String getName() {
        return name;
    }

    public Set<SubCommand> getSubCommands() {
        return subCommands;
    }

    public List<String> getTab() {
        return tab;
    }

    public abstract void runPlayer(Player player, String[] args);
    public abstract void runConsole(CommandSender sender, String[] args);
    public void onlyPlayer(CommandSender sender) {
        sender.sendMessage("Only players can use this command!");
    }
}
