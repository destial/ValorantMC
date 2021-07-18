package xyz.destiall.mc.valorant.factories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.Armour;
import xyz.destiall.mc.valorant.api.Gun;
import com.shampaggon.crackshot.CSUtility;

public class ItemFactory {
    private static Gun CLASSIC;
    private static final CSUtility CS_UTILITY = new CSUtility();
    public static Gun createGun(String name, Integer price, Material material, int damage, int ammo, float reloadSpeed, float fireSpeed) {
        Gun.Name gunName = Gun.Name.valueOf(name);
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + gunName.name().replace("_", " "));
        stack.setItemMeta(meta);
        Gun gun = new Gun(gunName, stack, damage, ammo, fireSpeed, reloadSpeed, price);
        if (gunName.equals(Gun.Name.CLASSIC)) {
            CLASSIC = gun;
        }
        return gun;
    }

    public static Armour createArmour(String name, Integer price, Material material, Integer amount) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name.toUpperCase().replace("_", " "));
        stack.setItemMeta(meta);
        return new Armour(stack, amount, price);
    }

    public static ItemStack createCrackshotGun(String name) {
        return CS_UTILITY.generateWeapon(name);
    }

    public static Gun GET_CLASSIC() {
        return CLASSIC;
    }
}
