package xyz.destiall.mc.valorant.classes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.destiall.mc.valorant.agents.jett.BladeStorm;
import xyz.destiall.mc.valorant.agents.jett.CloudBurst;
import xyz.destiall.mc.valorant.agents.jett.Updraft;
import xyz.destiall.mc.valorant.agents.reyna.Leer;
import xyz.destiall.mc.valorant.api.Gun;
import xyz.destiall.mc.valorant.api.Knife;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.Settings;
import xyz.destiall.mc.valorant.api.Spike;
import xyz.destiall.mc.valorant.api.Team;
import xyz.destiall.mc.valorant.api.Ultimate;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.utils.Economy;

import java.util.HashMap;

public class ParticipantImpl implements Participant {
    private final Player player;
    private final Team team;
    private final Economy econ;
    private final Knife knife;
    private final HashMap<Ability, Integer> abilities = new HashMap<>();
    private Gun primary;
    private Gun secondary;
    private Agent agent;
    private Spike spike;
    private Settings.Chat chatSettings;
    private int kills;
    private int deaths;
    private int assists;
    private boolean dead;
    private boolean flashed;
    private boolean ultimate;
    private boolean usingUlt;

    public ParticipantImpl(Player player, Team team) {
        this.player = player;
        this.team = team;
        kills = 0;
        deaths = 0;
        assists = 0;
        primary = null;
        secondary = ItemFactory.GET_CLASSIC();
        knife = new Knife(new ItemStack(Material.IRON_SWORD));
        ItemMeta meta = knife.getItem().getItemMeta();
        meta.setDisplayName("Knife");
        knife.getItem().setItemMeta(meta);
        agent = null;
        flashed = false;
        dead = false;
        ultimate = false;
        usingUlt = false;
        spike = null;
        chatSettings = Settings.Chat.TEAM;
        econ = new Economy(500);
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
    public void toTeam() {
        player.teleport(team.getSpawn());
    }

    @Override
    public Agent getAgent() {
        return agent;
    }

    @Override
    public HashMap<Ability, Integer> getAbilities() {
        return abilities;
    }

    @Override
    public Ultimate getUlt() {
        return (Ultimate) abilities.keySet().stream().filter(a -> a instanceof Ultimate).findFirst().orElse(null);
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
    public Settings.Chat getChatSettings() {
        return chatSettings;
    }

    @Override
    public void holdSpike(Spike spike) {
        this.spike = spike;
    }

    @Override
    public void setChatSettings(Settings.Chat setting) {
        this.chatSettings = setting;
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
    public boolean isFlashed() {
        return flashed;
    }

    @Override
    public void setFlashed(boolean flashed) {
        this.flashed = flashed;
    }

    @Override
    public void setAwaitUlt(boolean ult) {
        this.ultimate = ult;
    }

    @Override
    public void setUseUlt(boolean ult) {
        this.usingUlt = ult;
    }

    @Override
    public void chooseAgent(Agent agent) {
        if (abilities.size() != 0) return;
        this.agent = agent;
        switch (agent) {
            case JETT: {
                Updraft up = new Updraft();
                abilities.put(up, up.getMaxUses());
                CloudBurst cb = new CloudBurst();
                abilities.put(cb, cb.getMaxUses());
                abilities.put(new BladeStorm(), 0);
                break;
            }
            case REYNA: {
                Leer l = new Leer();
                abilities.put(l, l.getMaxUses());
                break;
            }
            default: break;
        }
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public boolean isAwaitingUlt() {
        return ultimate;
    }

    @Override
    public boolean isUsingUlt() {
        return usingUlt;
    }

    @Override
    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
