package xyz.destiall.mc.valorant.api.items;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Spike;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Drop {
    private final Set<UUID> looking = ConcurrentHashMap.newKeySet();
    private final Match match;
    private final ArmorStand text;
    private final ItemStack stack;
    private final Item item;
    private final ScheduledTask renderTask;
    private Gun gun;
    private Spike spike;

    public Drop(Match match, Item item, ItemStack stack) {
        this.match = match;
        this.item = item;
        this.stack = stack;
        text = Effects.createPickupName(item);
        renderTask = Scheduler.repeat(() -> {
            for (VPlayer player : match.getPlayers().values()) {
                if (player.isDead() || !player.getPlayer().isOnline()) continue;
                Location eye = player.getEyeLocation();
                RayTraceResult result = eye.getWorld().rayTraceEntities(eye, eye.getDirection(), 3, e -> e instanceof Item);
                if (result != null && result.getHitEntity() == item) {
                    if (looking.add(player.getUUID())) {
                        Effects.sendArmorStand(text, player.getPlayer());
                        Effects.teleportArmorStand(item.getLocation(), text, player.getPlayer());
                    }
                } else {
                    if (looking.remove(player.getUUID())) {
                        Effects.removeArmorStand(text, player.getPlayer());
                    }
                }
            }
        }, 5L);
    }

    public ItemStack getStack() {
        return stack;
    }

    public void remove() {
        if (renderTask != null) {
            renderTask.cancel();
        }
        item.remove();
        for (VPlayer player : match.getPlayers().values()) {
            if (looking.remove(player.getUUID())) {
                Effects.removeArmorStand(text, player.getPlayer());
            }
        }
        looking.clear();
    }

    public Gun getGun() {
        return gun;
    }

    public Item getItem() {
        return item;
    }

    public Spike getSpike() {
        return spike;
    }

    public void setGun(Gun gun) {
        this.gun = gun;
    }

    public void setSpike(Spike spike) {
        this.spike = spike;
    }
}
