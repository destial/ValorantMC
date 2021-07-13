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
    Integer getDeaths();
    Integer getAssists();
    Agent getAgent();
    Gun getPrimaryGun();
    Gun getSecondaryGun();
    Knife getKnife();
    boolean isHoldingSpike();
    Economy getEconomy();
    default Match getMatch() {
        return getTeam().getMatch();
    }
    void addArmour(Integer armour);
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
