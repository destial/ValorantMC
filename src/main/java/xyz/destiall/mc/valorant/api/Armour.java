package xyz.destiall.mc.valorant.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Armour implements ShopItem, Giveable {
    private final Integer armour;
    private final ItemStack display;
    private final Integer price;
    public Armour(ItemStack display, Integer amount, Integer price) {
        this.armour = amount;
        this.display = display;
        this.price = price;
    }

    public Integer getArmount() {
        return armour;
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public void give(Participant participant) {

    }

    @Override
    public ItemStack getShopDisplay() {
        ItemStack clone = display.clone();
        ItemMeta meta = clone.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add("Price: $" + getPrice());
        clone.setItemMeta(meta);
        return clone;
    }

    @Override
    public Integer getPrice() {
        return price;
    }
}
