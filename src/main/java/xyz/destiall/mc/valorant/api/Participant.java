package xyz.destiall.mc.valorant.api;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
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
    HashMap<Integer, Ability> getAbilities();
    Gun getPrimaryGun();
    Gun getSecondaryGun();
    Knife getKnife();
    Spike getSpike();
    boolean isHoldingSpike();
    boolean isFlashed();
    boolean isDead();

    void setPrimaryGun(Gun gun);
    void setSecondaryGun(Gun gun);
    void setAgent(Agent agent);
    void addKill();
    void addDeath();
    void addAssist();
    void setDead(boolean dead);
    void holdSpike(Spike spike);
    void setFlashed(boolean flashed);
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
    default void showActionBar(String message) {
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
    default void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }
    default UUID getUUID() {
        return getPlayer().getUniqueId();
    }
}
