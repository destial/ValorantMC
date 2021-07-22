package xyz.destiall.mc.valorant.api;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.utils.Economy;

import java.util.HashMap;
import java.util.UUID;

public interface Participant {
    Player getPlayer();
    Team getTeam();
    Economy getEconomy();
    Integer getKills();
    Integer getDeaths();
    Integer getAssists();
    Agent getAgent();
    HashMap<Ability, Integer> getAbilities();
    Ultimate getUlt();
    Gun getPrimaryGun();
    Gun getSecondaryGun();
    Knife getKnife();
    Spike getSpike();
    Settings.Chat getChatSettings();
    boolean isHoldingSpike();
    boolean isFlashed();
    boolean isDead();
    boolean isAwaitingUlt();
    boolean isUsingUlt();

    void setPrimaryGun(Gun gun);
    void setSecondaryGun(Gun gun);
    void setAgent(Agent agent);
    void addKill();
    void addDeath();
    void addAssist();
    void toTeam();
    void setDead(boolean dead);
    void holdSpike(Spike spike);
    void setChatSettings(Settings.Chat setting);
    void setFlashed(boolean flashed);
    void setAwaitUlt(boolean ult);
    void setUseUlt(boolean ult);
    void chooseAgent(Agent agent);

    default void addArmour(Integer armour) {
        getPlayer().setAbsorptionAmount(armour / 100F * 20);
    }
    default void applyDefaultSet() {
        getKnife().give(this);
        ItemFactory.GET_CLASSIC().give(this);
        getPlayer().getInventory().setItem(1, null);
        getPlayer().getInventory().setHeldItemSlot(2);
    }
    default Match getMatch() {
        return getTeam().getMatch();
    }
    default void showHotbar(String message) {
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
    }
    default void showTitle(String message) {
        showTitle(message, null);
    }
    default void showTitle(String message, String subtitle) {
        getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', message), subtitle);
    }
    default void sendMessage(String message) {
        getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    default Location getLocation() {
        return getPlayer().getLocation();
    }
    default Location getEyeLocation() {
        return getPlayer().getEyeLocation();
    }
    default Vector getDirection() {
        return getLocation().getDirection();
    }
    default UUID getUUID() {
        return getPlayer().getUniqueId();
    }
}
