package xyz.destiall.mc.valorant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.listeners.SovaListener;

import java.util.Collections;

public class GunCommand extends SubCommand {
    public GunCommand() {
        super("gun");
        tab.add("operator");
        tab.add("shockbow");
        tab.add("radarbow");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        if (args.length == 0) return;
        if (args[0].equalsIgnoreCase("radarbow")) {
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemMeta meta = bow.getItemMeta();
            meta.setDisplayName(SovaListener.SOVA_BOW_NAME + " (1)");
            meta.setLore(Collections.singletonList(ChatColor.YELLOW + "RADAR"));
            bow.setItemMeta(meta);
            player.getInventory().addItem(bow);
            return;
        }
        if (args[0].equalsIgnoreCase("shockbow")) {
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemMeta meta = bow.getItemMeta();
            meta.setDisplayName(SovaListener.SOVA_BOW_NAME + " (1)");
            meta.setLore(Collections.singletonList(ChatColor.YELLOW + "SHOCK"));
            bow.setItemMeta(meta);
            player.getInventory().addItem(bow);
        }
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
