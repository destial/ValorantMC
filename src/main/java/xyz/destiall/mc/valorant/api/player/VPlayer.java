package xyz.destiall.mc.valorant.api.player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Ultimate;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Knife;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Economy;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.Spike;
import xyz.destiall.mc.valorant.database.Stats;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.utils.Formatter;

import java.util.HashMap;
import java.util.UUID;

public interface VPlayer {
    Player getPlayer();
    Team getTeam();
    Party getParty();
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
    Stats getStats();
    boolean isHoldingSpike();
    boolean isFlashed();
    boolean isDead();
    boolean isAwaitingUlt();
    boolean isUsingUlt();

    void setPrimaryGun(Gun gun);
    void setSecondaryGun(Gun gun);
    void setAgent(Agent agent);
    void setTeam(Team team);
    void setParty(Party party);
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
    void save();

    default void addArmour(Integer armour) {
        getPlayer().setAbsorptionAmount(armour / 100F * 20);
    }
    default void applyDefaultSet() {
        getPlayer().getInventory().clear();
        getKnife().give(this);
        ItemFactory.GET_CLASSIC().give(this);
        getPlayer().getInventory().setItem(0, null);
        getPlayer().getInventory().setHeldItemSlot(1);
    }
    default Match getMatch() {
        return getTeam().getMatch();
    }
    default void showHotbar(String message) {
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Formatter.color(message)));
    }
    default void showTitle(String message) {
        showTitle(message, null);
    }
    default void showTitle(String message, String subtitle) {
        getPlayer().sendTitle(Formatter.color(message), Formatter.color(subtitle), 0, 1, 0);
    }
    default void showSubTitle(String subtitle) {
        getPlayer().sendTitle(null, Formatter.color(subtitle), 0, 1, 0);
    }
    default void sendMessage(String message) {
        getPlayer().sendMessage(Formatter.color(message));
    }
    default Location getLocation() {
        return getPlayer().getLocation().clone();
    }
    default Location getEyeLocation() {
        return getPlayer().getEyeLocation().clone();
    }
    default Vector getDirection() {
        return getLocation().getDirection().clone();
    }
    default UUID getUUID() {
        return getPlayer().getUniqueId();
    }
}
