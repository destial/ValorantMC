package xyz.destiall.mc.valorant.factories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.Armour;
import xyz.destiall.mc.valorant.api.Gun;

public class ItemFactory {
    public static Gun createGun(String name, Integer price, Material material, int damage, int ammo, float reloadSpeed, float fireSpeed) {
        Gun.Name gunName = Gun.Name.valueOf(name);
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + gunName.name().replace("_", " "));
        stack.setItemMeta(meta);
        return new Gun(gunName, stack, damage, ammo, fireSpeed, reloadSpeed, price);
    }

    public static Armour createArmour(String name, Integer price, Material material, Integer amount) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name.toUpperCase().replace("_", " "));
        stack.setItemMeta(meta);
        return new Armour(stack, amount, price);
    }
}
