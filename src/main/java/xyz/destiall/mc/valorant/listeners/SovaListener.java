package xyz.destiall.mc.valorant.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class SovaListener implements Listener {

    @EventHandler
    public void onArrowHitBlock(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) return;
        Arrow arrow = (Arrow) e.getEntity();
        boolean isSovaArrow = !arrow.getMetadata("valorant_sova").isEmpty();
        if (!isSovaArrow) return;
        int rebounds = arrow.getMetadata("valorant_sova_rebounds").get(0).asInt();
        // TODO: Add Sova arrows
        if (e.getHitBlock() != null) {
            return;
        }
        if (e.getHitEntity() != null) {

        }
    }
}
