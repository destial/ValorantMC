package xyz.destiall.mc.valorant.api.items;

import org.bukkit.inventory.ItemStack;

public interface ShopItem {
    ItemStack getShopDisplay();
    Integer getPrice();
    class AbilityPlaceholder implements ShopItem {
        public AbilityPlaceholder() {}

        @Override
        public ItemStack getShopDisplay() {
            return null;
        }

        @Override
        public Integer getPrice() {
            return null;
        }
    }
}
