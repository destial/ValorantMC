package xyz.destiall.mc.valorant.api;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.factories.ItemFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {
    private static final HashMap<Integer, ShopItem> ITEMS = new HashMap<>();
    private final HashMap<Participant, Inventory> playerShop = new HashMap<>();
    private final Match match;
    public Shop(Match match) {
        this.match = match;
    }

    public static void setup() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Valorant.getInstance().getPlugin().getDataFolder(), "shop.yml"));
        for (String key : config.getConfigurationSection("guns").getKeys(false)) {
            String name = config.getString("guns." + key + ".name", "").toUpperCase();
            Integer price = config.getInt("guns." + key + ".price", 500);
            Material material = Material.valueOf(config.getString("guns." + key + ".item", "IRON_HORSE_ARMOR"));
            int damage = config.getInt("guns." + key + ".damage", 10);
            int ammo = config.getInt("guns." + key + ".ammo", 30);
            float fireSpeed = (float) config.getDouble("guns." + key + ".fireSpeed", 2D);
            float reloadSpeed = (float) config.getDouble("guns." + key + ".reloadSpeed", 5D);
            Gun gun = ItemFactory.createGun(name, price, material, damage, ammo, reloadSpeed, fireSpeed);
            if (gun == null) continue;
            ITEMS.put(Integer.parseInt(key), gun);
        }
        ItemFactory.saveCSFile();
        for (String key : config.getConfigurationSection("armor").getKeys(false)) {
            String name = config.getString("armor." + key + ".name", "LIGHT ARMOR");
            Integer amount = config.getInt("armor." + key + ".amount", 25);
            Integer price = config.getInt("armor." + key + ".price", 500);
            Material material = Material.valueOf(config.getString("armor." + key + ".item"));
            Armor armour = ItemFactory.createArmour(name, price, material, amount);
            ITEMS.put(Integer.parseInt(key), armour);
        }
        for (String abilitySlot : config.getStringList("ability.slots")) {
            ShopItem.AbilityPlaceholder placeholder = new ShopItem.AbilityPlaceholder();
            ITEMS.put(Integer.parseInt(abilitySlot), placeholder);
        }
    }

    public Inventory create(Participant participant) {
        if (playerShop.get(participant) != null) {
            return playerShop.get(participant);
        }
        Inventory inv = Bukkit.createInventory(null, 36, "Buy Shop");
        playerShop.put(participant, inv);
        List<Integer> slots = new ArrayList<>();
        for (Map.Entry<Integer, ShopItem> entry : ITEMS.entrySet()) {
            if (entry.getValue() instanceof ShopItem.AbilityPlaceholder) {
                slots.add(entry.getKey());
                continue;
            }
            inv.setItem(entry.getKey(), entry.getValue().getShopDisplay());
        }
        int i = 0;
        for (Ability ability : participant.getAbilities().keySet()) {
            if (ability instanceof Ultimate) continue;
            Integer slot = slots.get(i);
            inv.setItem(slot, ability.getShopDisplay());
            i++;
        }
        return inv;
    }

    public void open(Participant participant) {
        participant.getPlayer().openInventory(create(participant));
    }

    public void close() {
        for (Participant participant : match.getPlayers().values()) {
            participant.getPlayer().closeInventory();
        }
    }

    public Match getMatch() {
        return match;
    }

    public void buy(Participant participant, Integer slot) {
        ShopItem item = ITEMS.get(slot);
        if (item == null) return;
        if (item instanceof Gun) {
            Gun gun = (Gun) item;
            if (gun.getType().equals(Gun.Type.PISTOL) || gun.getType().equals(Gun.Type.TACTICAL)) {
                Gun secondary = participant.getSecondaryGun();
                if (secondary.getName().equals(gun.getName())) return;
            } else {
                Gun primary = participant.getPrimaryGun();
                if (primary.getName().equals(gun.getName())) return;
            }
        }
        if (item instanceof Ability) {

        }
        if (participant.getEconomy().getBalance() >= item.getPrice()) {
            if (item instanceof Giveable) {
                ((Giveable) item).give(participant);
                participant.getEconomy().remove(item.getPrice());
                return;
            }
            //if (item instanceof Ability) {
            //    ((Ability) item).addUses();
            //}
        }
    }
}
