package xyz.destiall.mc.valorant.classes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.Gun;
import xyz.destiall.mc.valorant.api.Knife;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.Spike;
import xyz.destiall.mc.valorant.api.Team;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.utils.Economy;

public class ParticipantImpl implements Participant {
    private final Player player;
    private final Match match;
    private final Team team;
    private final Economy econ;
    private Gun primary;
    private Gun secondary;
    private Knife knife;
    private Agent agent;
    private int kills;
    private int deaths;
    private int assists;
    private boolean dead;
    private boolean flashed;
    private Spike spike;
    public ParticipantImpl(Player player, Team team) {
        this.player = player;
        this.match = team.getMatch();
        this.team = team;
        kills = 0;
        deaths = 0;
        assists = 0;
        primary = null;
        secondary = ItemFactory.createGun("CLASSIC", 0, Material.WOODEN_HOE, 5, 30, 1.5F, 1.5F);
        knife = new Knife(new ItemStack(Material.WOODEN_SWORD));
        agent = null;
        flashed = false;
        dead = false;
        spike = null;
        econ = new Economy();
    }
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public Integer getKills() {
        return kills;
    }

    @Override
    public void addKill() {
        kills++;
    }

    @Override
    public Integer getDeaths() {
        return deaths;
    }

    @Override
    public void addDeath() {
        deaths++;
    }

    @Override
    public Integer getAssists() {
        return assists;
    }

    @Override
    public void addAssist() {
        assists++;
    }

    @Override
    public Agent getAgent() {
        return agent;
    }

    @Override
    public Gun getPrimaryGun() {
        return primary;
    }

    @Override
    public void setPrimaryGun(Gun gun) {
        this.primary = gun;
    }

    @Override
    public Gun getSecondaryGun() {
        return secondary;
    }

    @Override
    public void setSecondaryGun(Gun gun) {
        this.secondary = gun;
    }

    @Override
    public Knife getKnife() {
        return knife;
    }

    @Override
    public boolean isHoldingSpike() {
        return spike != null;
    }

    @Override
    public Spike getSpike() {
        return spike;
    }

    @Override
    public void holdSpike(Spike spike) {
        this.spike = spike;
    }

    @Override
    public Economy getEconomy() {
        return econ;
    }

    @Override
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void addArmour(Integer armour) {
        player.setAbsorptionAmount(armour / 100F * 20);
    }

    @Override
    public boolean isFlashed() {
        return flashed;
    }

    @Override
    public void setFlashed(boolean flashed) {
        this.flashed = flashed;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
