package xyz.destiall.mc.valorant.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Effects;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.HashMap;
import java.util.stream.Collectors;

public class SovaListener implements Listener {
    public static final String SOVA_BOW_NAME = ChatColor.BLUE + "Sova Bow " + ChatColor.AQUA;
    private final HashMap<Arrow, ScheduledTask> arrows = new HashMap<>();

    @EventHandler
    public void onSovaShoot(EntityShootBowEvent e) {
        if (!e.getBow().getItemMeta().getDisplayName().startsWith(SOVA_BOW_NAME)) return;
        if (!(e.getProjectile() instanceof Arrow arrow)) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        ItemMeta meta = e.getBow().getItemMeta();
        String chrgRaw = meta.getLore().get(1).substring((ChatColor.YELLOW + "Charges: " + ChatColor.RED).length());
        double charges = Double.parseDouble(chrgRaw);
        if (charges == 0) {
            e.setCancelled(true);
            return;
        }
        --charges;
        meta.getLore().remove(1);
        meta.getLore().add(ChatColor.YELLOW + "Charges: " + ChatColor.RED + charges);
        int index = meta.getDisplayName().indexOf("«");
        int amt = Integer.parseInt(String.valueOf(meta.getDisplayName().charAt(++index)));
        int type = 1;
        String string = meta.getLore().get(0);
        if (string.contains("SONAR")) {
            type = 0;
        }
        e.getBow().setItemMeta(meta);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setCustomName(ChatColor.BLUE + (type == 1 ? "Shock Dart" : "Radar Dart"));
        arrow.setMetadata("valorant_sova_rebounds", new FixedMetadataValue(Valorant.getInstance().getPlugin(), amt));
        arrow.setMetadata("valorant_sova_type", new FixedMetadataValue(Valorant.getInstance().getPlugin(), type));
        ScheduledTask task = Scheduler.repeat(() -> {
            VPlayer VPlayer = MatchManager.getInstance().getPlayer(player);
            Effects.dartTravel(arrow.getLocation(), VPlayer);
            if (arrow.isDead()) {
                arrows.get(arrow).cancel();
            }
        }, 1L);
        arrows.put(arrow, task);
    }

    @EventHandler
    public void onSovaChangeRebound(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_AIR) {
            onSovaDrawBow(e);
            return;
        }
        if (e.getItem() == null) return;
        ItemMeta meta = e.getItem().getItemMeta();
        if (!meta.getDisplayName().startsWith(SOVA_BOW_NAME)) return;
        e.getPlayer().playSound(e.getPlayer().getEyeLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
        int index = meta.getDisplayName().indexOf("«");
        int amt = Integer.parseInt(String.valueOf(meta.getDisplayName().charAt(++index)));
        if (amt == 2) {
            amt = 0;
        } else {
            ++amt;
        }
        String displayName = meta.getDisplayName();
        displayName = displayName.replace(String.valueOf(amt == 1 ? 2 : 1), String.valueOf(amt));
        meta.setDisplayName(displayName);
        e.getItem().setItemMeta(meta);
    }

    public void onSovaDrawBow(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) return;
        if (e.getPlayer().getItemInUse() == null) return;
        if (e.getItem() == null) return;
        if (!e.getItem().equals(e.getPlayer().getItemInUse())) return;
        ItemMeta meta = e.getItem().getItemMeta();
        if (!meta.getDisplayName().startsWith(SOVA_BOW_NAME)) return;
        String chrgRaw = meta.getLore().get(1).substring((ChatColor.YELLOW + "Charges: " + ChatColor.RED).length());
        double charges = Double.parseDouble(chrgRaw);
        if (charges == 0) e.setCancelled(true);
    }

    @EventHandler
    public void onSovaArrow(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;
        Arrow arrow = (Arrow) e.getEntity();
        Player player = (Player) e.getEntity().getShooter();
        Integer rebounds = getRebounds(arrow);
        Integer type = getArrowType(arrow);
        if (rebounds == null) return;
        if (type == null) return;
        if (rebounds == 0) {
            // 1: Shock, 0: Radar
            if (type == 1) {
                Effects.shockDart(arrow.getLocation());
                for (Entity entity : arrow.getNearbyEntities(4, 4, 4).stream().filter(en -> en instanceof LivingEntity).collect(Collectors.toList())) {
                    LivingEntity live = (LivingEntity) entity;
                    EntityDamageEvent edmg = new EntityDamageEvent(arrow, EntityDamageEvent.DamageCause.PROJECTILE, 15);
                    live.setLastDamageCause(edmg);
                    Bukkit.getPluginManager().callEvent(edmg);
                    if (edmg.isCancelled()) continue;
                    live.damage(15);
                }
            } else {
                for (Entity entity : arrow.getNearbyEntities(25, 25, 25).stream().filter(en -> en instanceof LivingEntity).collect(Collectors.toList())) {
                    LivingEntity live = (LivingEntity) entity;
                    Vector faceDirection = live.getLocation().getDirection().clone().subtract(arrow.getLocation().getDirection().clone()).normalize();
                    Location arrowLoc = arrow.getLocation().clone();
                    while (arrowLoc.distance(arrow.getLocation()) <= 25) {
                        arrowLoc.add(faceDirection);
                        if (arrowLoc.getBlock().rayTrace(arrow.getLocation(), faceDirection, 25, FluidCollisionMode.ALWAYS) == null) break;
                    }
                    if (entity instanceof Player) {
                        Player p = (Player) live;
                        p.playSound(p.getEyeLocation(), Sound.BLOCK_LEVER_CLICK, 1, 9);
                    }
                    live.setGlowing(true);
                    Scheduler.delay(() -> {
                        live.setGlowing(false);
                        Scheduler.delay(() -> {
                            if (live instanceof Player) {
                                Player p = (Player) live;
                                p.playSound(p.getEyeLocation(), Sound.BLOCK_LEVER_CLICK, 1, 9);
                            }
                            live.setGlowing(true);
                            Scheduler.delay(() -> {
                                live.setGlowing(false);
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
            Arrow arrow1 = copyArrow(arrow);
            arrow1.setVelocity(v);
            arrow1.getLocation().setDirection(v.clone().normalize());
            arrow1.setMetadata("valorant_sova_rebounds", new FixedMetadataValue(Valorant.getInstance().getPlugin(), rebounds));
            arrow1.setMetadata("valorant_sova_type", new FixedMetadataValue(Valorant.getInstance().getPlugin(), type));
            ScheduledTask task = Scheduler.repeat(() -> {
                VPlayer VPlayer = MatchManager.getInstance().getPlayer(player);
                Effects.dartTravel(arrow.getLocation(), VPlayer);
                if (arrow1.isDead()) {
                    arrows.get(arrow1).cancel();
                }
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

    private Arrow copyArrow(Arrow arrow) {
        Arrow arrow1 = (Arrow) arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.ARROW);
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
        return arrow1;
    }
}
