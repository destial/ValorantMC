package xyz.destiall.mc.valorant.api.events.weapon;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SniperShotEvent extends EntityDamageByEntityEvent {
    private final boolean headshot;
    public SniperShotEvent(Entity damager, Entity damagee, boolean headshot) {
        super(damager, damagee, DamageCause.CUSTOM, 1000);
        this.headshot = headshot;
    }

    public boolean isHeadshot() {
        return headshot;
    }
}
