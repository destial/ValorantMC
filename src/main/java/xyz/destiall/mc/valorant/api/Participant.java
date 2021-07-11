package xyz.destiall.mc.valorant.api;

import org.bukkit.entity.Player;

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
    Match getMatch();
    Integer getBalance();
}
