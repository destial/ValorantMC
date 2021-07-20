package xyz.destiall.mc.valorant.factories;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSMinion;
import com.shampaggon.crackshot.CSUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.Armor;
import xyz.destiall.mc.valorant.api.Gun;

import java.io.File;
import java.io.IOException;

public class ItemFactory {
    private static Gun CLASSIC;
    private static final CSUtility CS_UTILITY = new CSUtility();
    private static final CSDirector CS_DIRECTOR = CSDirector.getPlugin(CSDirector.class);
    private static final File CS_WEAPON_FILE = new CSMinion(CS_DIRECTOR).grabDefaults("defaultWeapons.yml");
    private static final YamlConfiguration CS_WEAPON_CONFIG = YamlConfiguration.loadConfiguration(CS_WEAPON_FILE);
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
        generateCrackshotGun(gun);
        return gun;
    }

    public static Armor createArmour(String name, Integer price, Material material, Integer amount) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name.toUpperCase().replace("_", " "));
        stack.setItemMeta(meta);
        return new Armor(stack, amount, price);
    }

    public static ItemStack createCrackshotGun(String name) {
        return CS_UTILITY.generateWeapon(name);
    }

    public static void generateCrackshotGun(Gun gun) {
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Item_Name", gun.getName().name() );
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Item_Type", gun.getItem().getType().name());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Item_Lore", "&e" + gun.getType().name());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Sounds_Acquired", "BAT_TAKEOFF-1-1-0");
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Skip_Name_Check", true);

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Cancel_Left_Click_Block_Damage", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Cancel_Right_Click_Interactions", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Delay_Between_Shots", gun.getFireSpeed());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Recoil_Amount", 0);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Projectile_Amount", 1);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Projectile_Type", "snowball");
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Projectile_Speed", 1000);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Projectile_Damage", gun.getDamage());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Bullet_Spread", 1.5);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Sounds_Shoot", "IRONGOLEM_HIT-1-2-0,SKELETON_HURT-1-2-0,ZOMBIE_WOOD-1-2-0");

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Sneak.Enable", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Sneak.No_Recoil", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Sneak.Bullet_Spread", 0.8);

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Enable", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Reload_Amount", gun.getMaxAmmo());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Reload_Duration", gun.getReloadSpeed());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Sounds_Reloading", "FIRE_IGNITE-1-1-4,DOOR_OPEN-1-2-6,FIRE_IGNITE-1-1-36,HURT_FLESH-1-0-37,DOOR_CLOSE-1-2-38");

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Headshot.Enable", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Headshot.Bonus_Damage", 100);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Headshot.Sounds_Shooter", "NOTE_PLING-1-2-0");

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Abilities.Reset_Hit_Cooldown", true);

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Fully_Automatic.Enable", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Fully_Automatic.Fire_Rate", (int) gun.getFireSpeed());

        if (gun.getType().equals(Gun.Type.RIFLE) || gun.getType().equals(Gun.Type.SMG) || gun.getType().equals(Gun.Type.MACHINE) || gun.getName().equals(Gun.Name.SHERIFF)) {
            CS_WEAPON_CONFIG.set(gun.getName().name()  + ".Scope.Enable", true);
            CS_WEAPON_CONFIG.set(gun.getName().name()  + ".Scope.Zoom_Amount", 2);
            CS_WEAPON_CONFIG.set(gun.getName().name()  + ".Scope.Zoom_Bullet_Spread", 0.5);
            CS_WEAPON_CONFIG.set(gun.getName().name()  + ".Scope.Sounds_Toggle_Zoom", "ENDERDRAGON_WINGS-1-2-0");
        }
    }

    public static void saveCSFile() {
        try {
            CS_WEAPON_CONFIG.save(CS_WEAPON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Gun GET_CLASSIC() {
        return CLASSIC;
    }
}
