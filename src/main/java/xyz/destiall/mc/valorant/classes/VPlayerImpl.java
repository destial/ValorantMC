package xyz.destiall.mc.valorant.classes;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.agents.cypher.CyberCage;
import xyz.destiall.mc.valorant.agents.jett.BladeStorm;
import xyz.destiall.mc.valorant.agents.jett.CloudBurst;
import xyz.destiall.mc.valorant.agents.jett.Tailwind;
import xyz.destiall.mc.valorant.agents.jett.Updraft;
import xyz.destiall.mc.valorant.agents.omen.Paranoia;
import xyz.destiall.mc.valorant.agents.omen.ShroudedStep;
import xyz.destiall.mc.valorant.agents.phoenix.Blaze;
import xyz.destiall.mc.valorant.agents.reyna.Leer;
import xyz.destiall.mc.valorant.api.abilities.Ability;
import xyz.destiall.mc.valorant.api.abilities.Agent;
import xyz.destiall.mc.valorant.api.abilities.PreviewHold;
import xyz.destiall.mc.valorant.api.abilities.Ultimate;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Knife;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.map.Site;
import xyz.destiall.mc.valorant.api.match.Economy;
import xyz.destiall.mc.valorant.api.match.Spike;
import xyz.destiall.mc.valorant.api.player.Party;
import xyz.destiall.mc.valorant.api.player.Settings;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.api.topbar.TopbarHandler;
import xyz.destiall.mc.valorant.database.Datastore;
import xyz.destiall.mc.valorant.database.Stats;
import xyz.destiall.mc.valorant.factories.ItemFactory;
import xyz.destiall.mc.valorant.listeners.MatchListener;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

class VPlayerImpl implements VPlayer {
    private final AtomicReference<Player> player;
    private final Economy econ;
    private final Knife knife;
    private final HashMap<Integer, Ability> abilities = new HashMap<>();
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
    private int ultPoints;
    private boolean dead;
    private boolean flashed;
    private boolean usingUlt;
    private ScheduledTask diffusingTask;
    private ScheduledTask plantingTask;
    private ScheduledTask beforePlant;
    private PreviewHold holdingAbility;
    private Event damageEvent;

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
        flashed = dead = usingUlt = false;
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
        flashed = dead = usingUlt = false;
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
        return party;
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
    public HashMap<Integer, Ability> getAbilities() {
        return abilities;
    }

    @Override
    public Ultimate getUlt() {
        return (Ultimate) abilities.values().stream().filter(a -> a instanceof Ultimate).findFirst().orElse(null);
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
    public PreviewHold getHoldingAbility() {
        return holdingAbility;
    }

    @Override
    public Event getLastDamage() {
        return damageEvent;
    }

    @Override
    public int getUltPoints() {
        return ultPoints;
    }

    @Override
    public void holdSpike(Spike spike) {
        this.spike = spike;
        if (spike == null) {
            getPlayer().getInventory().setItem(3, null);
            if (beforePlant != null) {
                beforePlant.cancel();
                beforePlant = null;
            }
            return;
        }
        if (spike.getDrop() != null) spike.getDrop().remove();
        if (!getPlayer().isOnline()) return;

        getPlayer().getInventory().remove(spike.getItem());
        getPlayer().getInventory().setItem(3, spike.getItem());
        sendMessage("&cYou are holding the spike!");

        beforePlant = Scheduler.repeat(() -> {
            for (Site site : getMatch().getMap().getSites()) {
                site.render(this, getMatch().getMap());
            }
        }, 5L);
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
    public void setUseUlt(boolean ult) {
        this.usingUlt = ult;
    }

    @Override
    public void setUltPoints(int points) {
        this.ultPoints = points;
    }

    @Override
    public void addUltPoint(int points) {
        this.ultPoints = points;
    }

    @Override
    public void chooseAgent(Agent agent) {
        if (abilities.size() != 0) return;
        if (agent == null) {
            Collection<VPlayer> list = getMatch().getPlayers().values();
            agent = Arrays.stream(Agent.values()).filter(a -> list.stream().noneMatch(p -> p.getAgent() == a)).findFirst().orElseThrow();
        }
        this.agent = agent;
        switch (agent) {
            case JETT -> {
                abilities.put(5, new Updraft(this));
                abilities.put(6, new Tailwind(this));
                abilities.put(7, new CloudBurst(this));
                // abilities.put(8, new BladeStorm(this));
            }
            case REYNA -> abilities.put(5, new Leer(this));
            case PHOENIX -> abilities.put(5, new Blaze(this));
            case CYPHER -> abilities.put(5, new CyberCage(this));
            case OMEN -> {
                abilities.put(5, new Paranoia(this));
                abilities.put(6, new ShroudedStep(this));
            }
            default -> {}
        }

        Scheduler.delayAsync(() -> {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("applyskin");
                out.writeUTF(getPlayer().getName());
                out.writeUTF(this.agent.VALUE);
                out.writeUTF(this.agent.SIGNATURE);
                getPlayer().sendPluginMessage(Valorant.getInstance().getPlugin(), "valorant:channel", b.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0L);
    }

    @Override
    public void save() {
        Datastore.getInstance().updatePlayer(this);
    }

    @Override
    public void setDiffusing(boolean diffusing) {
        Spike sp = getMatch().getSpike();
        if (diffusing && diffusingTask == null) {
            String s = "???";
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
                sp.setDiffuse(t + 1 / 160f);
                getInventory().setHeldItemSlot(3);
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
        }
    }

    @Override
    public void setPlanting(boolean planting) {
        Spike sp = getMatch().getSpike();
        if (planting && plantingTask == null) {
            String s = "???";
            plantingTask = Scheduler.repeat(() -> {
                StringBuilder builder = new StringBuilder();
                float t = sp.getPlant();
                for (float i = 0; i < 10; i++) {
                    ChatColor color = ChatColor.GRAY;
                    if (i / 10f <= t) {
                        color = ChatColor.BLUE;
                    }
                    builder.append(color).append(s);
                }
                getPlayer().sendTitle(builder.toString(), null, 0, 2, 0);
                sp.setPlant(t + 1 / 75f);
                getInventory().setHeldItemSlot(3);
                if (sp.getPlant() >= 1f) {
                    sp.place(this, getLocation());
                    sp.setPlant(1f);
                }
            }, 1L);
            return;
        }
        if (!planting && plantingTask != null) {
            plantingTask.cancel();
            plantingTask = null;
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
    public void leave(boolean tp) {
        getPlayer().closeInventory();
        for (ItemStack item : getPlayer().getInventory()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (isHoldingSpike() && item.isSimilar(getMatch().getSpike().getItem())) {
                Item drop = getPlayer().getWorld().dropItem(getLocation(), item);
                MatchListener.spikeDropped(this, drop);
                continue;
            }
            if ((getPrimaryGun() != null && item.isSimilar(getPrimaryGun().getItem())) || (getSecondaryGun() != null && item.isSimilar(getSecondaryGun().getItem()))) {
                getPlayer().getWorld().dropItem(getLocation(), item);
            }
        }
        getPlayer().getInventory().clear();
        getTeam().removeMember(getUUID());
        TopbarHandler topbar = getMatch().getModule(TopbarHandler.class);
        if (topbar != null) {
            topbar.leave(this);
        }
        getPlayer().setHealth(20d);
        setDead(false);
        if (tp) getPlayer().teleport(MatchManager.getInstance().getLobby());
    }

    @Override
    public void setHoldingAbility(PreviewHold hold) {
        this.holdingAbility = hold;
    }

    @Override
    public void setLastDamage(Event e) {
        this.damageEvent = e;
    }

    @Override
    public boolean isDiffusing() {
        return diffusingTask != null;
    }

    @Override
    public boolean isPlanting() {
        return plantingTask != null;
    }

    @Override
    public boolean isDead() {
        return dead;
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
