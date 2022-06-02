package xyz.destiall.mc.valorant.factories;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSMinion;
import com.shampaggon.crackshot.CSUtility;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.items.Gun;

import java.io.File;
import java.io.IOException;

public class CSItemFactory {
    private static final CSUtility CS_UTILITY = new CSUtility();
    private static final CSDirector CS_DIRECTOR = CS_UTILITY.getHandle();
    private static final CSMinion CS_MINION = CS_DIRECTOR.csminion;
    private static final File CS_WEAPON_FILE = CS_MINION.grabDefaults("defaultWeapons.yml");
    private static final YamlConfiguration CS_WEAPON_CONFIG = YamlConfiguration.loadConfiguration(CS_WEAPON_FILE);

    public static ItemStack createCrackshotGun(String name) {
        return CS_UTILITY.generateWeapon(name);
    }

    public static void generateCrackshotGun(Gun gun) {
        if (gun.getName().getType().equals(Gun.Type.SNIPER)) return;
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Item_Name", gun.getName().name() );
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Item_Type", gun.getItem().getType().name());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Item_Lore", "&e" + gun.getType().name());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Sounds_Acquired", "BAT_TAKEOFF-1-1-0");
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Item_Information.Skip_Name_Check", false);

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Cancel_Left_Click_Block_Damage", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Cancel_Right_Click_Interactions", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Delay_Between_Shots", gun.getFireSpeed() * 20);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Recoil_Amount", 0);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Remove_Arrows_On_Impact", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Remove_Bullet_Drop", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Projectile_Amount", 1);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Projectile_Type", "arrow");
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Projectile_Speed", 100);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Projectile_Damage", gun.getDamage());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Bullet_Spread", 0.5);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Shooting.Sounds_Shoot", "IRONGOLEM_HIT-1-2-0,SKELETON_HURT-1-2-0,ZOMBIE_WOOD-1-2-0");

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Sneak.Enable", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Sneak.No_Recoil", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Sneak.Bullet_Spread", 0.0);

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Enable", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Reload_Amount", gun.getMaxAmmo());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Starting_Amount", gun.getMaxAmmo());
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Reload_Duration", gun.getReloadSpeed() * 20);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Reload_Shoot_Delay", gun.getReloadSpeed() * 20);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Reload.Sounds_Reloading", "FIRE_IGNITE-1-1-4,DOOR_OPEN-1-2-6,FIRE_IGNITE-1-1-36,HURT_FLESH-1-0-37,DOOR_CLOSE-1-2-38");

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Headshot.Enable", true);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Headshot.Bonus_Damage", gun.getDamage() + 50);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Headshot.Sounds_Shooter", "NOTE_PLING-1-2-0");

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Abilities.Reset_Hit_Cooldown", true);

        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Fully_Automatic.Enable", false);
        CS_WEAPON_CONFIG.set(gun.getName().name() + ".Fully_Automatic.Fire_Rate", (int) gun.getFireSpeed());

        if (gun.getType().equals(Gun.Type.RIFLE) || gun.getType().equals(Gun.Type.SMG) || gun.getType().equals(Gun.Type.MACHINE) || gun.getName().equals(Gun.Name.SHERIFF)) {
            CS_WEAPON_CONFIG.set(gun.getName().name()  + ".Scope.Enable", true);
            CS_WEAPON_CONFIG.set(gun.getName().name()  + ".Scope.Zoom_Amount", 2);
            CS_WEAPON_CONFIG.set(gun.getName().name()  + ".Scope.Zoom_Bullet_Spread", 0);
            CS_WEAPON_CONFIG.set(gun.getName().name()  + ".Scope.Sounds_Toggle_Zoom", "ENDERDRAGON_WINGS-1-2-0");
        }
    }

    public static void saveCSFile() {
        try {
            CS_WEAPON_CONFIG.save(CS_WEAPON_FILE);
            CS_MINION.loadWeapons(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
