package xyz.destiall.mc.valorant.api;

import org.bukkit.entity.Player;
import xyz.destiall.mc.valorant.utils.Economy;

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
}
