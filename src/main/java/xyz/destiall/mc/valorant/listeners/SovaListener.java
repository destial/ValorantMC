package xyz.destiall.mc.valorant.listeners;

import com.comphenix.protocol.PacketType;
import com.github.fierioziy.particlenativeapi.api.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SovaListener implements Listener {
    public static final String SOVA_BOW_NAME = ChatColor.BLUE + "SOVA BOW ";
    private final HashMap<Arrow, ScheduledTask> arrows = new HashMap<>();

    @EventHandler
    public void onSovaShoot(EntityShootBowEvent e) {
        if (!e.getBow().getItemMeta().getDisplayName().startsWith(SOVA_BOW_NAME)) return;
        if (!(e.getProjectile() instanceof Arrow)) return;
        ItemMeta meta = e.getBow().getItemMeta();
        Arrow arrow = (Arrow) e.getProjectile();
        int index = meta.getDisplayName().indexOf("(");
        int amt = Integer.parseInt(String.valueOf(meta.getDisplayName().charAt(++index)));
        int type = 1;
        if (meta.hasLore()) {
            String string = meta.getLore().get(0);
            if (string.toLowerCase().contains("radar")) {
                type = 0;
            }
        }
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setCustomName(e.getEntity().getName() + "'s " + ChatColor.BLUE + (type == 1 ? "Shock Dart" : "Radar Dart"));
        arrow.setMetadata("valorant_sova_rebounds", new FixedMetadataValue(Valorant.getInstance().getPlugin(), amt));
        arrow.setMetadata("valorant_sova_type", new FixedMetadataValue(Valorant.getInstance().getPlugin(), type));
        ScheduledTask task = Scheduler.repeat(() -> {
           Effects.smokeTravel(arrow.getLocation(), Agent.SOVA);
        }, 1L);
        arrows.put(arrow, task);
    }

    @EventHandler
    public void onSovaChangeRebound(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_AIR) return;
        if (e.getItem() == null) return;
        if (!e.getItem().getItemMeta().getDisplayName().startsWith(SOVA_BOW_NAME)) return;
        e.getPlayer().playSound(e.getPlayer().getEyeLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
        ItemMeta meta = e.getItem().getItemMeta();
        int index = meta.getDisplayName().indexOf("(");
        int amt = Integer.parseInt(String.valueOf(meta.getDisplayName().charAt(++index)));
        if (amt == 1) {
            amt = 2;
        } else if (amt == 2) {
            amt = 1;
        }
        String displayName = meta.getDisplayName();
        displayName = displayName.replace(String.valueOf(amt == 1 ? 2 : 1), String.valueOf(amt));
        meta.setDisplayName(displayName);
        e.getItem().setItemMeta(meta);
    }

    @EventHandler
    public void onSovaArrow(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) return;
        Arrow arrow = (Arrow) e.getEntity();
        Integer rebounds = getRebounds(arrow);
        Integer type = getArrowType(arrow);
        if (rebounds == null) return;
        if (type == null) return;
        if (rebounds == 0) {
            // 1: Shock, 0: Radar
            if (type == 1) {
                Effects.shockDart(arrow.getLocation());
                for (Entity entity : arrow.getNearbyEntities(4, 4, 4)) {
                    entity.setLastDamageCause(new EntityDamageEvent(arrow, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 15));
                }
            } else {
                for (Entity entity : arrow.getNearbyEntities(5, 5, 5)) {
                    if (entity instanceof Player) {
                        Player p = (Player) entity;
                        p.playSound(p.getEyeLocation(), Sound.BLOCK_LEVER_CLICK, 1, 9);
                    }
                    entity.setGlowing(true);
                    Scheduler.delay(() -> {
                        entity.setGlowing(false);
                        Scheduler.delay(() -> {
                            if (entity instanceof Player) {
                                Player p = (Player) entity;
                                p.playSound(p.getEyeLocation(), Sound.BLOCK_LEVER_CLICK, 1, 9);
                            }
                            entity.setGlowing(true);
                            Scheduler.delay(() -> {
                                entity.setGlowing(false);
                            }, 20L);
                        }, 20L);
                    }, 20L);
                }
            }
            arrows.get(arrow).cancel();
            arrow.remove();
            return;
        }
        arrows.get(arrow).cancel();
        if (e.getHitBlock() != null && e.getHitBlockFace() != null) {
            --rebounds;
            Vector N = new Vector(e.getHitBlockFace().getDirection().getX(), e.getHitBlockFace().getDirection().getY(), e.getHitBlockFace().getDirection().getZ());
            Vector u = new Vector(arrow.getVelocity().getX(), arrow.getVelocity().getY(), arrow.getVelocity().getZ());
            if (u.length() < 0.3) return;
            Vector v = u.clone().subtract(N.clone().multiply(u.clone().dot(N.clone()) * 2));
            v.multiply(0.6);
            e.setCancelled(true);
            arrow.remove();
            Arrow arrow1 = (Arrow) arrow.getWorld().spawnEntity(arrow.getLocation().add(v), EntityType.ARROW);
            arrow1.setVelocity(v);
            arrow1.getLocation().setDirection(v.clone().normalize());
            arrow1.getLocation().setWorld(arrow.getWorld());
            arrow1.setShooter(arrow.getShooter());
            arrow1.setDamage(arrow.getDamage());
            arrow1.setGravity(arrow.hasGravity());
            arrow1.setFallDistance(arrow.getFallDistance());
            arrow1.setSilent(arrow.isSilent());
            arrow1.setVisualFire(arrow.isVisualFire());
            arrow1.setBounce(arrow.doesBounce());
            arrow1.setShotFromCrossbow(arrow.isShotFromCrossbow());
            arrow1.setPierceLevel(arrow.getPierceLevel());
            arrow1.setInvulnerable(arrow.isInvulnerable());
            arrow1.setKnockbackStrength(arrow.getKnockbackStrength());
            arrow1.setCritical(arrow.isCritical());
            arrow1.setCustomName(arrow.getCustomName());
            arrow1.setCustomNameVisible(arrow.isCustomNameVisible());
            arrow1.setPersistent(arrow.isPersistent());
            arrow1.setGlowing(arrow.isGlowing());
            arrow1.setTicksLived(arrow.getTicksLived());
            arrow1.setFreezeTicks(arrow.getFreezeTicks());
            arrow1.setFireTicks(arrow.getFireTicks());
            arrow1.setOp(arrow.isOp());
            arrow1.setPickupStatus(arrow.getPickupStatus());
            arrow1.setMetadata("valorant_sova_rebounds", new FixedMetadataValue(Valorant.getInstance().getPlugin(), rebounds));
            arrow1.setMetadata("valorant_sova_type", new FixedMetadataValue(Valorant.getInstance().getPlugin(), type));
            ScheduledTask task = Scheduler.repeat(() -> {
                Effects.smokeTravel(arrow1.getLocation(), Agent.SOVA);
            }, 1L);
            arrows.put(arrow1, task);
            return;
        }
    }

    private Integer getRebounds(Entity arrow) {
        MetadataValue reboundMetaValue = arrow.getMetadata("valorant_sova_rebounds").stream().filter(m -> m.getOwningPlugin() == Valorant.getInstance().getPlugin()).findFirst().orElse(null);
        return reboundMetaValue == null ? null : reboundMetaValue.asInt();
    }

    private Integer getArrowType(Entity arrow) {
        MetadataValue reboundMetaValue = arrow.getMetadata("valorant_sova_type").stream().filter(m -> m.getOwningPlugin() == Valorant.getInstance().getPlugin()).findFirst().orElse(null);
        return reboundMetaValue == null ? null : reboundMetaValue.asInt();
    }
}
