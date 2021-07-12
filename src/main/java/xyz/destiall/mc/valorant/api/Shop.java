package xyz.destiall.mc.valorant.api;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.factories.ItemFactory;

import java.io.File;
import java.util.*;
import java.util.Map;

public class Shop {
    private final HashMap<Integer, ShopItem> items = new HashMap<>();
    private final HashMap<Participant, Inventory> playerShop = new HashMap<>();
    private final Match match;
    public Shop(Match match) {
        this.match = match;
        create();
    }

    private void create() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Valorant.getInstance().getPlugin().getDataFolder(), "shop.yml"));
        for (String key : config.getConfigurationSection("guns").getKeys(false)) {
            String name = config.getString("guns." + key + ".name", "").toUpperCase();
            Integer price = config.getInt("guns." + key + ".price", 500);
            Material material = Material.valueOf(config.getString("guns." + key + ".item"));
            int damage = config.getInt("guns." + key + ".damage", 10);
            int ammo = config.getInt("guns." + key + ".ammo", 30);
            float fireSpeed = (float) config.getDouble("guns." + key + ".fireSpeed", 2D);
            float reloadSpeed = (float) config.getDouble("guns." + key + ".reloadSpeed", 5D);
            Gun gun = ItemFactory.createGun(name, price, material, damage, ammo, reloadSpeed, fireSpeed);
            items.put(Integer.parseInt(key), gun);
        }
        for (String key : config.getConfigurationSection("armour").getKeys(false)) {
            String name = config.getString("armour." + key + ".name", "LIGHT ARMOR");
            Integer amount = config.getInt("armour." + key + ".amount", 25);
            Integer price = config.getInt("armour." + key + ".price", 500);
            Material material = Material.valueOf(config.getString("armour." + key + ".item"));
            Armour armour = ItemFactory.createArmour(name, price, material, amount);
            items.put(Integer.parseInt(key), armour);
        }
        for (String abilitySlot : config.getStringList("ability.slots")) {
            ShopItem.AbilityPlaceholder placeholder = new ShopItem.AbilityPlaceholder();
            items.put(Integer.parseInt(abilitySlot), placeholder);
        }
        for (Participant participant : match.getPlayers().values()) {
            Inventory inv = Bukkit.createInventory(null, 54, "Buy Shop");
            playerShop.put(participant, inv);
            List<Integer> slots = new ArrayList<>();
            for (Map.Entry<Integer, ShopItem> entry : items.entrySet()) {
                if (entry.getValue() instanceof ShopItem.AbilityPlaceholder) {
                    slots.add(entry.getKey());
                    continue;
                }
                inv.setItem(entry.getKey(), entry.getValue().getShopDisplay());
            }
            int i = 0;
            for (Ability ability : participant.getAgent().getAbilites()) {
                if (ability instanceof Ultimate) continue;
                Integer slot = slots.get(i);
                inv.setItem(slot, ability.getShopDisplay());
                i++;
            }
        }
    }

    public void open(Participant participant) {
        participant.getPlayer().openInventory(playerShop.get(participant));
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
        ShopItem item = items.get(slot);
        if (item == null) return;
        if (participant.getEconomy().getBalance() >= item.getPrice()) {
            participant.getEconomy().remove(item.getPrice());
            ((Giveable) item).give(participant);
        }
    }
}
