package xyz.destiall.mc.valorant.classes;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.events.match.MatchCompleteEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchStartEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.events.match.SwitchingSidesEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundFinishEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundStartEvent;
import xyz.destiall.mc.valorant.api.items.Gun;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.match.AgentPicker;
import xyz.destiall.mc.valorant.api.match.Countdown;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.MatchResult;
import xyz.destiall.mc.valorant.api.match.Module;
import xyz.destiall.mc.valorant.api.match.Round;
import xyz.destiall.mc.valorant.api.match.Shop;
import xyz.destiall.mc.valorant.api.match.Spike;
import xyz.destiall.mc.valorant.api.player.Party;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.database.Datastore;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Debugger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchImpl implements Match {
    private final Set<Team> teams = new HashSet<>();
    private final List<Round> rounds = new ArrayList<>();
    private final HashMap<VPlayer, ItemStack[]> inventories = new HashMap<>();
    private final Map map;
    private final int id;
    private final List<Module> modules = new ArrayList<>();
    private final Set<Gun> droppedGuns = new HashSet<>();
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
    public Set<Gun> getDroppedGuns() {
        return droppedGuns;
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
        Countdown countdown = getModule(Countdown.class);
        removeModule(countdown);
        if (!isComplete()) nextRound();
        else end(MatchTerminateEvent.Reason.COMPLETE);
    }

    private void startRound() {
        Debugger.debug("Starting round");
        spike = new Spike(this);
        addModule(spike);
        rounds.add(new RoundImpl(rounds.size() + 1));
        if (rounds.size() / 7 == 1) switchSides();
        buyPeriod = true;
        final Countdown startingCountdown = new Countdown(Countdown.Context.ROUND_STARTING);
        addModule(startingCountdown);
        for (BoundingBox wall : map.getWalls()) {
            for (double x = wall.getMinX(); x <= wall.getMaxX(); x++) {
                for (double y = wall.getMinY(); y <= wall.getMaxY(); y++) {
                    for (double z = wall.getMinZ(); z <= wall.getMaxZ(); z++) {
                        Block block = map.getWorld().getBlockAt((int)x, (int)y, (int)z);
                        if (block.getType() == Material.AIR) block.setType(Material.BLUE_STAINED_GLASS);
                    }
                }
            }
        }
        for (VPlayer p : getPlayers().values()) {
            startingCountdown.getBossBar().addPlayer(p.getPlayer());
            if (p.isDead()) {
                if (p.getPlayer().isDead()) {
                    p.getPlayer().spigot().respawn();
                }
                p.applyDefaultSet();
            }
            p.toTeam();
            p.getPlayer().setGameMode(GameMode.ADVENTURE);
        }
        startingCountdown.start();
        startingCountdown.onComplete(() -> {
            Debugger.debug("Playing round");
            callEvent(new RoundStartEvent(this));
            for (BoundingBox wall : map.getWalls()) {
                for (double x = wall.getMinX(); x <= wall.getMaxX(); x++) {
                    for (double y = wall.getMinY(); y <= wall.getMaxY(); y++) {
                        for (double z = wall.getMinZ(); z <= wall.getMaxZ(); z++) {
                            Block block = map.getWorld().getBlockAt((int)x, (int)y, (int)z);
                            if (block.getType() == Material.BLUE_STAINED_GLASS) block.setType(Material.AIR);
                        }
                    }
                }
            }
            buyPeriod = false;
            startingCountdown.stop();
            removeModule(startingCountdown);
            final Countdown startedCountdown = new Countdown(Countdown.Context.BEFORE_SPIKE);
            addModule(startedCountdown);
            for (VPlayer p : getPlayers().values()) {
                startedCountdown.getBossBar().addPlayer(p.getPlayer());
            }
            Location spikeDrop = getAttacker().getSpawn().clone().add(getAttacker().getSpawn().getDirection().clone().multiply(2));
            map.getWorld().dropItem(spikeDrop, spike.getItem());
            startedCountdown.start();
            startedCountdown.onComplete(() -> {
                getDefender().addScore();
                endRound();
            });
        });
    }

    @Override
    public void nextRound() {
        if (rounds.size() != 0) {
            Countdown countdown = getModule(Countdown.class);
            if (countdown != null) removeModule(countdown);
            final Countdown c = new Countdown(Countdown.Context.ROUND_ENDING);
            addModule(c);
            for (VPlayer vPlayer : getPlayers().values()) {
                c.getBossBar().addPlayer(vPlayer.getPlayer());
            }
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
            AgentPicker agentPicker = getModule(AgentPicker.class);
            if (agentPicker == null) return false;
            removeModule(agentPicker);
            nextRound();
            return true;
        }
        end(MatchTerminateEvent.Reason.COMPLETE);
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
        Location loc = MatchManager.getInstance().getLobby();
        for (VPlayer p : getPlayers().values()) {
            p.getPlayer().getInventory().clear();
            ItemStack[] stacks = inventories.get(p);
            p.getPlayer().getInventory().addItem(stacks);
            p.getPlayer().teleport(loc);
            if (getWinningTeam() == p.getTeam()) {
                p.getStats().addWin(result);
                p.getStats().addXP(100);
            } else {
                p.getStats().addLoss(result);
                p.getStats().addXP(10);
            }
            p.save();
        }
        inventories.clear();
        Datastore.getInstance().saveMatch(result);
        teams.clear();
        callEvent(new MatchCompleteEvent(this));
        return result;
    }

    @Override
    public void terminate() {
        end(MatchTerminateEvent.Reason.HACK);
    }

    @Override
    public void join(Player player) {
        Team team = teams.stream().min(Comparator.comparingInt(a -> a.getMembers().size())).orElse(null);
        if (team == null) return;
        if ((5 - team.getMembers().size()) < 1) return;
        VPlayer p = new VPlayerImpl(player, team);
        team.getMembers().add(p);
        inventories.put(p, player.getInventory().getContents().clone());
        player.getInventory().clear();
        Datastore.getInstance().loadPlayer(p);
        AgentPicker picker = getModule(AgentPicker.class);
        picker.show(p);
    }

    @Override
    public void joinTeam(Team.Side side, Player player) {
        Team team = teams.stream().filter(t -> t.getSide().equals(side)).findFirst().orElse(null);
        if (team == null) return;
        VPlayer p = new VPlayerImpl(player, team);
        team.getMembers().add(p);
        inventories.put(p, player.getInventory().getContents().clone());
        player.getInventory().clear();
        Datastore.getInstance().loadPlayer(p);
        AgentPicker picker = getModule(AgentPicker.class);
        picker.show(p);
    }

    @Override
    public void joinParty(Party party) {
        Team team = teams.stream().min(Comparator.comparingInt(a -> a.getMembers().size())).orElse(null);
        if (team == null) return;
        if (party.getMembers().size() > (5 - team.getMembers().size())) return;
        for (VPlayer p : party.getMembers()) {
            p.setTeam(team);
            team.getMembers().add(p);
            inventories.put(p, p.getPlayer().getInventory().getContents().clone());
            p.getPlayer().getInventory().clear();
            AgentPicker picker = getModule(AgentPicker.class);
            picker.show(p);
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
        if (hasModule(countdown.getClass())) {
            modules.remove(getModule(countdown.getClass()));
        }
        modules.add(countdown);
    }

    @Override
    public Collection<Module> getModules() {
        return modules;
    }

    @Override
    public <N extends Module> N getModule(Class<N> key) {
        return (N) modules.stream().filter(m -> m.getClass().isAssignableFrom(key)).findFirst().orElse(null);
    }
}
