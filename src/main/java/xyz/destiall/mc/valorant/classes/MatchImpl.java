package xyz.destiall.mc.valorant.classes;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.topbar.TopbarHandler;
import xyz.destiall.mc.valorant.api.events.countdown.CountdownStartEvent;
import xyz.destiall.mc.valorant.api.events.countdown.CountdownStopEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchCompleteEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchStartEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.events.match.SwitchingSidesEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundFinishEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundStartEvent;
import xyz.destiall.mc.valorant.api.items.Drop;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.match.AgentPicker;
import xyz.destiall.mc.valorant.api.match.Countdown;
import xyz.destiall.mc.valorant.api.match.DropHandler;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.MatchResult;
import xyz.destiall.mc.valorant.api.match.Module;
import xyz.destiall.mc.valorant.api.match.Round;
import xyz.destiall.mc.valorant.api.match.Shop;
import xyz.destiall.mc.valorant.api.match.Spike;
import xyz.destiall.mc.valorant.api.player.Party;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.api.sidebar.BukkitSidebar;
import xyz.destiall.mc.valorant.api.sidebar.SidebarHandler;
import xyz.destiall.mc.valorant.database.Datastore;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MatchImpl implements Match {
    private final List<Module> modules = new ArrayList<>();
    private final Set<Team> teams = new HashSet<>();
    private final List<Round> rounds = new ArrayList<>();
    private final HashMap<VPlayer, ItemStack[]> inventories = new HashMap<>();
    private final ConcurrentHashMap<Item, Drop> drops = new ConcurrentHashMap<>();
    private final Map map;
    private final int id;
    private boolean buyPeriod;
    private Spike spike;
    private MatchState state;

    public MatchImpl(Map map, int id) {
        this.id = id;
        this.map = map;
        buyPeriod = true;
        teams.add(new TeamImpl(this, Team.Side.ATTACKER));
        teams.add(new TeamImpl(this, Team.Side.DEFENDER));
        addModule(new Shop(this));
        addModule(new AgentPicker(this));
        addModule(new DropHandler(this));
        state = MatchState.WAITING;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public Set<Team> getTeams() {
        return teams;
    }

    @Override
    public Round getRound() {
        if (rounds.size() == 0) return null;
        return rounds.get(rounds.size() - 1);
    }

    @Override
    public List<Round> getRounds() {
        return rounds;
    }

    @Override
    public Map getMap() {
        return map;
    }

    @Override
    public Spike getSpike() {
        return spike;
    }

    @Override
    public ConcurrentHashMap<Item, Drop> getDroppedItems() {
        return drops;
    }

    @Override
    public boolean isBuyPeriod() {
        return buyPeriod;
    }

    @Override
    public boolean isWaitingForPlayers() {
        return hasModule(AgentPicker.class);
    }

    @Override
    public boolean isOver() {
        return state == MatchState.ENDING;
    }

    @Override
    public void switchSides() {
        Team attacker = getAttacker();
        Team defender = getDefender();
        attacker.setSide(Team.Side.DEFENDER);
        defender.setSide(Team.Side.ATTACKER);
        callEvent(new SwitchingSidesEvent(this));
    }

    @Override
    public void endRound() {
        callEvent(new RoundFinishEvent(this));
        setCountdown(null);
        //removeModule(DeadBodyHandler.class);
        Collection<VPlayer> list = getPlayers().values();
        for (VPlayer p : list) {
            if (p.isDiffusing()) p.setDiffusing(false);
        }
        if (!isComplete()) nextRound();
        else {
            Scheduler.delay(() -> end(MatchTerminateEvent.Reason.COMPLETE), 20L);
        }
    }

    private void startRound() {
        if (spike != null) {
            removeModule(spike);
            getModule(DropHandler.class).removeDrops();
        }

        if ((float) rounds.size() / 12f == 1f || rounds.size() > 24) switchSides();

        buyPeriod = true;
        final Countdown startingCountdown = new Countdown(Countdown.Context.ROUND_STARTING);
        setCountdown(startingCountdown);
        map.pullUpWalls();
        Collection<VPlayer> list = getPlayers().values();
        for (VPlayer p : list) {
            if (rounds.size() == 0 || p.isDead() || (float) rounds.size() / 12f == 1f) {
                if (p.getPlayer().isDead()) {
                    p.getPlayer().spigot().respawn();
                }
                p.applyDefaultSet();
            }
            p.setDead(false);
            p.toTeam();
            p.getPlayer().setGameMode(GameMode.SURVIVAL);
            p.getPlayer().setHealth(p.getPlayer().getMaxHealth());
        }
        rounds.add(new RoundImpl(rounds.size() + 1));

        addModule(spike = new Spike(this));
        //addModule(new DeadBodyHandler());
        Location spawn = map.getAttackerCenter();
        Location spikeDrop = spawn.add(spawn.getDirection().multiply(3));
        spike.setDrop(map.getWorld().dropItem(spikeDrop, spike.getItem()));
        startingCountdown.start();
        startingCountdown.onComplete(() -> {
            callEvent(new RoundStartEvent(this));
            map.pullDownWalls();
            buyPeriod = false;
            final Countdown startedCountdown = new Countdown(Countdown.Context.BEFORE_SPIKE);
            setCountdown(startedCountdown);
            startedCountdown.start();
            startedCountdown.onComplete(() -> {
                getDefender().addScore();
                getRound().setWinningSide(Team.Side.DEFENDER);
                endRound();
            });
        });
    }

    @Override
    public void nextRound() {
        if (rounds.size() != 0) {
            final Countdown c = new Countdown(Countdown.Context.ROUND_ENDING);
            setCountdown(c);
            c.start();
            c.onComplete(() -> {
                removeModule(c);
                startRound();
            });
        } else {
            startRound();
        }
    }

    @Override
    public boolean start(boolean force) {
        if (getPlayers().size() < 10 && !force) return false;
        if (getState() == MatchState.PLAYING) return false;
        setState(MatchState.PLAYING);
        MatchStartEvent e = new MatchStartEvent(this);
        callEvent(e);
        if (!e.isCancelled()) {
            addModule(new SidebarHandler(this, BukkitSidebar.class));
            addModule(new TopbarHandler(this));
            removeModule(AgentPicker.class);
            nextRound();
            return true;
        }
        end(MatchTerminateEvent.Reason.CANCEL);
        return false;
    }

    @Override
    public MatchResult end(MatchTerminateEvent.Reason reason) {
        state = MatchState.ENDING;
        map.setUse(false);
        modules.forEach(Module::destroy);
        modules.clear();
        if (!isComplete()) {
            callEvent(new MatchTerminateEvent(this, reason));
            return null;
        }
        MatchResult result = new MatchResult(this);
        result.save();
        callEvent(new MatchCompleteEvent(this));
        Location loc = MatchManager.getInstance().getLobby();
        Collection<VPlayer> list = getPlayers().values();
        for (VPlayer p : list) {
            if (!p.getPlayer().isOnline()) continue;
            if (getWinningTeam() == p.getTeam()) {
                p.getStats().addWin(p, result);
                p.getStats().addXP(100);
            } else {
                p.getStats().addLoss(p, result);
                p.getStats().addXP(10);
            }
            p.getPlayer().getInventory().clear();
            p.getPlayer().teleport(loc);
            ItemStack[] items = inventories.get(p);
            if (items != null) {
                p.getPlayer().getInventory().setContents(items);
            }
            p.save();
        }
        inventories.clear();
        for (Team team : teams) {
            team.getMembers().clear();
        }
        teams.clear();
        MatchManager.getInstance().removeMatch(this);
        return result;
    }

    @Override
    public void terminate() {
        end(MatchTerminateEvent.Reason.HACK);
    }

    @Override
    public void join(Player player) {
        Team team = teams.stream().min(Comparator.comparingInt(Team::getSize)).orElse(null);
        if (team == null) return;
        if ((5 - team.getSize()) < 1) return;
        VPlayer p = new VPlayerImpl(player, team);
        team.addMember(p);
        inventories.put(p, player.getInventory().getContents());
        player.getInventory().clear();
        Datastore.getInstance().loadPlayer(p);
        AgentPicker picker = getModule(AgentPicker.class);
        picker.show(p);
        player.setWalkSpeed(0.2f);
    }

    @Override
    public void joinTeam(Team.Side side, Player player) {
        Team team = teams.stream().filter(t -> t.getSide().equals(side)).findFirst().orElse(null);
        if (team == null) return;
        VPlayer p = new VPlayerImpl(player, team);
        team.addMember(p);
        inventories.put(p, player.getInventory().getContents());
        player.getInventory().clear();
        Datastore.getInstance().loadPlayer(p);
        AgentPicker picker = getModule(AgentPicker.class);
        picker.show(p);
        player.setWalkSpeed(0.2f);
    }

    @Override
    public void joinParty(Party party) {
        Team team = teams.stream().min(Comparator.comparingInt(Team::getSize)).orElse(null);
        if (team == null) return;
        if (party.getMembers().size() > (5 - team.getSize())) return;
        for (VPlayer p : party.getMembers()) {
            p.setTeam(team);
            team.addMember(p);
            inventories.put(p, p.getPlayer().getInventory().getContents());
            p.getPlayer().getInventory().clear();
            AgentPicker picker = getModule(AgentPicker.class);
            picker.show(p);
            p.getPlayer().setWalkSpeed(0.2f);
        }
    }

    @Override
    public MatchState getState() {
        return state;
    }

    @Override
    public void setState(MatchState state) {
        this.state = state;
    }

    @Override
    public void setCountdown(Countdown countdown) {
        if (hasModule(Countdown.class)) {
            Countdown c = getModule(Countdown.class);
            removeModule(Countdown.class);
            callEvent(new CountdownStopEvent(this, c));
        }
        if (countdown != null) {
            addModule(countdown);
            callEvent(new CountdownStartEvent(this, countdown));
        }
    }

    @Override
    public Collection<Module> getModules() {
        return modules;
    }
}
