package xyz.destiall.mc.valorant.factories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.items.Armor;
import xyz.destiall.mc.valorant.api.items.Gun;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemFactory {
    private static Gun CLASSIC;


    public static final Set<Gun> ALL_GUNS = new HashSet<>();

    public static Gun createGun(String name, Integer price, Material material, int damage, int ammo, float reloadSpeed, float fireSpeed) {
        Gun.Name gunName = Gun.Name.valueOf(name);
        if (ALL_GUNS.stream().anyMatch(g -> g.getName().equals(gunName))) return null;
        String gname = gunName.name();
        gname = gname.toLowerCase();
        gname = gname.substring(0, 1).toUpperCase() + gname.substring(1);
        gname = gname.replace("_", " ");
        ItemStack stack;
        Gun gun;
        if (gunName.getType() == Gun.Type.SNIPER) {
            stack = new ItemStack(Material.SPYGLASS, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + gname + ChatColor.WHITE + "«" + ammo + "»");
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW + "Type: " + ChatColor.GOLD + Gun.Type.SNIPER.name(),
                    ChatColor.YELLOW + "Damage: " + ChatColor.RED + damage
            ));
            stack.setItemMeta(meta);
            gun = new Gun(gunName, stack, damage, ammo, fireSpeed, reloadSpeed, price);
        } else {
            stack = new ItemStack(material);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + gname + ChatColor.WHITE + "«" + ammo + "»");
            stack.setItemMeta(meta);
            gun = new Gun(gunName, stack, damage, ammo, fireSpeed, reloadSpeed, price);
            if (gunName == Gun.Name.CLASSIC) {
                CLASSIC = gun;
            }
            if (Bukkit.getPluginManager().getPlugin("CrackShot") != null) {
                CSItemFactory.generateCrackshotGun(gun);
            }
        }
        ALL_GUNS.add(gun);
        return gun;
    }

    public static Armor createArmour(String name, Integer price, Material material, Integer amount) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name.toUpperCase().replace("_", " "));
        stack.setItemMeta(meta);
        return new Armor(stack, amount, price);
    }

    public static ItemStack generateGun(Gun.Name gun) {
        if (Bukkit.getPluginManager().getPlugin("CrackShot") != null) {
            return CSItemFactory.createCrackshotGun(gun.name());
        }
        return ALL_GUNS.stream().filter(g -> g.getName() == gun).findFirst().get().getItem().clone();
    }

    public static Gun getGun(ItemStack stack) {
        return ALL_GUNS.stream()
                .filter(g -> g.getItem().isSimilar(stack) || stack.getItemMeta().getDisplayName().toUpperCase().contains(g.getName().name()))
                .findFirst().orElse(null);
    }

    public static Gun GET_CLASSIC() {
        return CLASSIC;
    }
}
