package xyz.destiall.mc.valorant.classes;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.agents.jett.BladeStorm;
import xyz.destiall.mc.valorant.agents.jett.CloudBurst;
import xyz.destiall.mc.valorant.agents.jett.Updraft;
import xyz.destiall.mc.valorant.agents.phoenix.Blaze;
import xyz.destiall.mc.valorant.agents.reyna.Leer;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.Ultimate;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Knife;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.match.Economy;
import xyz.destiall.mc.valorant.api.match.Spike;
import xyz.destiall.mc.valorant.api.player.Party;
import xyz.destiall.mc.valorant.api.player.Settings;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.database.Datastore;
import xyz.destiall.mc.valorant.database.Stats;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class VPlayerImpl implements VPlayer {
    private final AtomicReference<Player> player;
    private final Economy econ;
    private final Knife knife;
    private final HashMap<Ability, Integer> abilities = new HashMap<>();
    private final Stats stats;
    private final Settings settings;
    private Party party;
    private Team team;
    private Gun primary;
    private Gun secondary;
    private Agent agent;
    private Spike spike;
    private int kills;
    private int deaths;
    private int assists;
    private boolean dead;
    private boolean flashed;
    private boolean ultimate;
    private boolean usingUlt;
    private ScheduledTask diffusingTask;

    public VPlayerImpl(Player player, Team team) {
        this.player = new AtomicReference<>(player);
        this.team = team;
        kills = deaths = assists = 0;
        primary = null;
        secondary = ItemFactory.GET_CLASSIC();
        knife = new Knife(new ItemStack(Material.IRON_SWORD));
        agent = null;
        spike = null;
        party = null;
        flashed = dead = ultimate = usingUlt = false;
        settings = new Settings();
        econ = new Economy(500);
        stats = new Stats(getUUID());
    }

    public VPlayerImpl(Player player, Party party) {
        this.player = new AtomicReference<>(player);
        this.party = party;
        team = null;
        primary = null;
        secondary = ItemFactory.GET_CLASSIC();
        knife = new Knife(new ItemStack(Material.IRON_SWORD));
        agent = null;
        spike = null;
        flashed = dead = ultimate = usingUlt = false;
        settings = new Settings();
        econ = new Economy(500);
        stats = new Stats(getUUID());
    }

    @Override
    public Player getPlayer() {
        return player.get();
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public Party getParty() {
        return null;
    }

    @Override
    public Integer getKills() {
        return kills;
    }

    @Override
    public void addKill() {
        stats.addKill();
        kills++;
    }

    @Override
    public Integer getDeaths() {
        return deaths;
    }

    @Override
    public void addDeath() {
        stats.addDeath();
        deaths++;
    }

    @Override
    public Integer getAssists() {
        return assists;
    }

    @Override
    public void addAssist() {
        stats.addAssist();
        assists++;
    }

    @Override
    public void toTeam() {
        if (!getPlayer().isOnline()) return;
        getPlayer().teleport(team.getSpawn());
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
        return settings.getChat();
    }

    @Override
    public Stats getStats() {
        return stats;
    }

    @Override
    public void holdSpike(Spike spike) {
        this.spike = spike;
        if (spike == null) return;
        spike.getDrop().remove();
        if (!getPlayer().isOnline()) return;
        getPlayer().getInventory().remove(spike.getItem());
        getPlayer().getInventory().setItem(3, spike.getItem());
        sendMessage("&cYou are holding the spike!");
    }

    @Override
    public void setChatSettings(Settings.Chat setting) {
        settings.setChat(setting);
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
    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public void setParty(Party party) {
        this.party = party;
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
        if (agent == null) {
            Collection<VPlayer> list = getMatch().getPlayers().values();
            agent = Arrays.stream(Agent.values()).filter(a -> list.stream().noneMatch(p -> p.getAgent() == a)).findFirst().orElse(Agent.CYPHER);
        }
        this.agent = agent;
        switch (agent) {
            case JETT: {
                Updraft up = new Updraft(this);
                abilities.put(up, up.getMaxUses());
                CloudBurst cb = new CloudBurst(this);
                abilities.put(cb, cb.getMaxUses());
                abilities.put(new BladeStorm(this), 0);
                break;
            }
            case REYNA: {
                Leer l = new Leer(this);
                abilities.put(l, l.getMaxUses());
                break;
            }
            case PHOENIX: {
                abilities.put(new Blaze(this), 0);
                break;
            }
            case CYPHER: {
                //abilities.put(new CyberCage(this), 0);
                break;
            }
            default: break;
        }
    }

    @Override
    public void save() {
        Datastore.getInstance().updatePlayer(this);
    }

    @Override
    public void setDiffusing(boolean diffusing) {
        Spike sp = getMatch().getSpike();
        if (diffusing && diffusingTask == null) {
            String s = "█";
            diffusingTask = Scheduler.repeat(() -> {
                StringBuilder builder = new StringBuilder();
                float t = sp.getDiffuse();
                for (float i = 0; i < 10; i++) {
                    ChatColor color = ChatColor.GRAY;
                    if (i / 10f <= t) {
                        color = ChatColor.BLUE;
                    }
                    builder.append(color).append(s);
                }
                getPlayer().sendTitle(builder.toString(), null, 0, 2, 0);
                sp.setDiffuse(t + 1/150f);
                if (sp.getDiffuse() >= 1f) {
                    sp.defuse();
                    sp.setDiffuse(1f);
                }
            }, 1L);
            return;
        }
        if (!diffusing && diffusingTask != null) {
            if (sp.getDiffuse() > 0.5) {
                sp.setDiffuse(0.49f);
            }
            diffusingTask.cancel();
            diffusingTask = null;
            return;
        }
    }

    @Override
    public void rejoin(Player player) {
        this.player.set(player);
        if (isDead()) {
            player.setGameMode(GameMode.SPECTATOR);
            getTeam().getMembers().stream().filter(t -> t != this).findFirst().ifPresent(spectateTarget -> player.setSpectatorTarget(spectateTarget.getPlayer()));
        }
        toTeam();
    }

    @Override
    public boolean isDiffusing() {
        return diffusingTask != null;
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
