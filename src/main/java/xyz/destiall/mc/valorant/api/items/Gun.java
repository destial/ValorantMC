package xyz.destiall.mc.valorant.api.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Shooter;

import java.util.HashMap;
import java.util.List;

public class Gun implements ShopItem, Giveable {
    protected final ItemStack itemStack;
    protected final Integer price;
    protected final Integer damage;
    protected float fireSpeed;
    protected float reloadSpeed;
    protected final Type type;
    protected final Name name;
    protected final Integer maxAmmo;
    protected final HashMap<Player, Long> shots = new HashMap<>();
    protected Integer currentAmmo;
    protected boolean aiming;

    public Gun(Name name, ItemStack stack, Integer damage, Integer ammo, float fireSpeed, float reloadSpeed, Integer price) {
        this.name = name;
        this.itemStack = stack;
        this.price = price;
        this.damage = damage;
        this.reloadSpeed = reloadSpeed;
        this.fireSpeed = fireSpeed;
        this.type = name.getType();
        this.maxAmmo = ammo;
        this.currentAmmo = ammo;
        aiming = false;
    }

    public Integer getDamage() {
        return damage;
    }

    public float getReloadSpeed() {
        return reloadSpeed;
    }

    public float getFireSpeed() {
        return fireSpeed;
    }

    public Type getType() {
        return type;
    }

    public Name getName() {
        return name;
    }

    public Integer getCurrentAmmo() {
        return currentAmmo;
    }

    public Integer getMaxAmmo() {
        return maxAmmo;
    }

    public void shoot(Player player) {
        Long before = shots.get(player);
        double spread = 0;
        if (before != null) {
            long now = System.currentTimeMillis();
            long diff = now - before;
            spread = 200D / diff;
        }
        shots.put(player, System.currentTimeMillis());
        Shooter.shoot(player, player.getEyeLocation().clone(), player.getLocation().getDirection().clone(), damage, spread);
    }

    public void setAiming(boolean aiming) {
        this.aiming = aiming;
    }

    public boolean isAiming() {
        return aiming;
    }

    @Override
    public ItemStack getShopDisplay() {
        ItemStack clone = itemStack.clone();
        ItemMeta meta = clone.getItemMeta();
        List<String> lores = meta.getLore();
        lores.add("Price: $" + price);
        clone.setItemMeta(meta);
        return clone;
    }

    @Override
    public ItemStack getItem() {
        return itemStack.clone();
    }

    @Override
    public void give(VPlayer VPlayer) {
        int slot = 2;
        if (!type.equals(Type.PISTOL)) {
            slot = 1;
        }
        if (slot == 1) {
            VPlayer.setPrimaryGun(this);
        } else {
            VPlayer.setSecondaryGun(this);
        }
    }

    @Override
    public Integer getPrice() {
        return price;
    }

    public enum Type {
        SMG,
        RIFLE,
        MACHINE,
        SNIPER,
        SHOTGUN,
        PISTOL,
        TACTICAL,
    }

    public enum Name {
        CLASSIC(Type.PISTOL),
        SHORTY(Type.PISTOL),
        FRENZY(Type.PISTOL),
        GHOST(Type.PISTOL),
        SHERIFF(Type.PISTOL),
        STINGER(Type.SMG),
        SPECTRE(Type.SMG),
        BULLDOG(Type.RIFLE),
        GUARDIAN(Type.RIFLE),
        PHANTOM(Type.RIFLE),
        VANDAL(Type.RIFLE),
        BUCKY(Type.SHOTGUN),
        JUDGE(Type.SHOTGUN),
        ARES(Type.MACHINE),
        ODIN(Type.MACHINE),
        MARSHAL(Type.SNIPER),
        OPERATOR(Type.SNIPER),
        TACTICAL(Type.TACTICAL);

        Type type;
        Name(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }
    }
}
