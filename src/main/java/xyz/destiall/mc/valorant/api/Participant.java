package xyz.destiall.mc.valorant.api;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.utils.Economy;

import java.util.UUID;

public interface Participant {
    Player getPlayer();
    Team getTeam();
    Integer getKills();
    void addKill();
    Integer getDeaths();
    void addDeath();
    Integer getAssists();
    void addAssist();
    Agent getAgent();
    Gun getPrimaryGun();
    void setPrimaryGun(Gun gun);
    Gun getSecondaryGun();
    void setSecondaryGun(Gun gun);
    Knife getKnife();
    boolean isHoldingSpike();
    Spike getSpike();
    void holdSpike(Spike spike);
    boolean isFlashed();
    void setFlashed(boolean flashed);
    boolean isDead();
    void setDead(boolean dead);
    Economy getEconomy();
    void setAgent(Agent agent);
    void addArmour(Integer armour);
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
