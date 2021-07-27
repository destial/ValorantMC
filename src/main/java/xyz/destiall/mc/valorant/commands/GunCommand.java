package xyz.destiall.mc.valorant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.listeners.SovaListener;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GunCommand extends SubCommand {
    public GunCommand() {
        super("gun");
        permission = "valorant.admin";
        tab.addAll(ItemFactory.ALL_GUNS.stream().map(g -> g.getName().name().toLowerCase()).collect(Collectors.toList()));
        tab.add("operator");
        tab.add("shockbow");
        tab.add("radarbow");
    }

    @Override
    public void runPlayer(Player player, String[] args) {
        if (args.length == 0) {
            ValorantCommand.sendError(player);
            return;
        }
        if (args[0].equalsIgnoreCase("radarbow")) {
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemMeta meta = bow.getItemMeta();
            meta.setDisplayName(SovaListener.SOVA_BOW_NAME + "«0»");
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW + "Bow Type: " + ChatColor.GOLD + "RADAR",
                    ChatColor.YELLOW + "Charges: " + ChatColor.RED + "2"
            ));
            bow.setItemMeta(meta);
            player.getInventory().addItem(bow);
            return;
        }
        if (args[0].equalsIgnoreCase("shockbow")) {
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemMeta meta = bow.getItemMeta();
            meta.setDisplayName(SovaListener.SOVA_BOW_NAME + "«0»");
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW + "Bow Type: " + ChatColor.GOLD + "SHOCK",
                    ChatColor.YELLOW + "Charges: " + ChatColor.RED + "2"
            ));
            bow.setItemMeta(meta);
            player.getInventory().addItem(bow);
            return;
        }
        if (args[0].equalsIgnoreCase("operator")) {
            ItemStack operator = new ItemStack(Material.SPYGLASS, 1);
            ItemMeta meta = operator.getItemMeta();
            String name = Gun.Name.OPERATOR.name();
            name = name.toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            meta.setDisplayName(ChatColor.YELLOW + name);
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW + "Type: " + ChatColor.GOLD + Gun.Name.OPERATOR.getType().name(),
                    ChatColor.YELLOW + "Damage: " + ChatColor.RED + "-1"
            ));
            operator.setItemMeta(meta);
            player.getInventory().addItem(operator);
        }
        Gun gun = ItemFactory.ALL_GUNS.stream().filter(g -> g.getName().name().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (gun == null) {
            player.sendMessage(ChatColor.RED + "That gun does not exist!");
            return;
        }
        player.getInventory().addItem(gun.getItem());
    }

    @Override
    public void runConsole(CommandSender sender, String[] args) {
        onlyPlayer(sender);
    }
}
