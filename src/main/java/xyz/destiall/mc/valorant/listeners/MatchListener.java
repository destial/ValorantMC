package xyz.destiall.mc.valorant.listeners;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.api.Gun;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.events.DeathEvent;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;
import xyz.destiall.mc.valorant.utils.Shooter;

import java.util.HashMap;

public class MatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Participant victim = MatchManager.getInstance().getParticipant(p);
        if (victim == null) return;
        Player k = p.getKiller();
        Participant killer = victim;
        if (k != null) {
            killer = victim.getMatch().getPlayers().get(k.getUniqueId());
        }
        if (killer == null) {
            killer = victim;
        }
        e.setDeathMessage(null);
        e.setDroppedExp(0);
        e.setKeepInventory(false);
        e.setKeepLevel(false);
        e.setNewTotalExp(0);
        e.setNewExp(0);
        e.setNewLevel(0);
        e.getDrops().clear();
        victim.addDeath();
        if (victim != killer) {
            killer.addKill();
        }
        victim.getMatch().callEvent(new DeathEvent(victim, killer));
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        Participant victim = MatchManager.getInstance().getParticipant((Player) e.getVictim());
        if (victim == null) return;
        Participant damager = MatchManager.getInstance().getParticipant(e.getPlayer());
        if (damager == null) return;
        if (victim.getTeam() == damager.getTeam()) {
            e.setCancelled(true);
            e.setDamage(0);
            e.getDamager().teleport(e.getDamager().getLocation().clone().add(e.getDamager().getVelocity().clone().normalize()));
        }
    }

    private final HashMap<Player, Boolean> list = new HashMap<>();
    private final HashMap<Player, Boolean> list2 = new HashMap<>();
    private final HashMap<Player, ScheduledTask> tasks = new HashMap<>();

    @EventHandler
    public void onPlayerScope(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_AIR || e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (!e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SPYGLASS)) return;
        ItemMeta meta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
        if (!meta.hasLore()) return;
        if (!meta.getLore().get(0).toUpperCase().contains(Gun.Type.SNIPER.name())) return;
        String dmgRaw = meta.getLore().get(1).substring(("Damage: " + ChatColor.RED).length());
        double dmg = Double.parseDouble(dmgRaw);
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            Boolean isHolding = list.computeIfAbsent(e.getPlayer(), k -> true);
            if (isHolding) {
                list2.put(e.getPlayer(), false);
                ScheduledTask task = Scheduler.repeat(() -> {
                    ScheduledTask t = tasks.get(e.getPlayer());
                    if (t.getTask().isCancelled()) return;
                    if (e.getPlayer().getItemInUse() == null || !e.getPlayer().getItemInUse().equals(e.getItem())) {
                        list.put(e.getPlayer(), false);
                        if (list2.get(e.getPlayer())) return;
                        list2.put(e.getPlayer(), true);
                        Shooter.snipe(e.getPlayer(), e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(), dmg, 0);
                        t.cancel();
                    }
                }, 1L);
                tasks.put(e.getPlayer(), task);
            } else {
                list.put(e.getPlayer(), true);
                Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(e.getPlayer(), e.getAction(), e.getItem(), e.getClickedBlock(), e.getBlockFace()));
            }
            return;
        }
        if (e.getPlayer().isSneaking()) {
            Shooter.snipe(e.getPlayer(), e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(), dmg, 1);
            return;
        }
        if (e.getPlayer().isSprinting()) {
            Shooter.snipe(e.getPlayer(), e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(), dmg, 3);
            return;
        }
        Shooter.snipe(e.getPlayer(), e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(), dmg, 2);
    }
}
