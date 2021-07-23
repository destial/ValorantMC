package xyz.destiall.mc.valorant.classes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.events.match.MatchCompleteEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchStartEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.events.match.SwitchingSidesEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundFinishEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundStartEvent;
import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.map.Map;
import xyz.destiall.mc.valorant.api.match.AgentPicker;
import xyz.destiall.mc.valorant.api.match.Countdown;
import xyz.destiall.mc.valorant.api.match.Match;
import xyz.destiall.mc.valorant.api.match.MatchResult;
import xyz.destiall.mc.valorant.api.match.Module;
import xyz.destiall.mc.valorant.api.match.Round;
import xyz.destiall.mc.valorant.api.match.Shop;
import xyz.destiall.mc.valorant.api.player.Participant;
import xyz.destiall.mc.valorant.database.Datastore;
import xyz.destiall.mc.valorant.managers.MatchManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchImpl implements Match {
    private final Set<Team> teams = new HashSet<>();
    private final List<Round> rounds = new ArrayList<>();
    private final HashMap<Participant, ItemStack[]> inventories = new HashMap<>();
    private final Map map;
    private final int id;
    private final List<Module> modules = new ArrayList<>();
    private boolean buyPeriod;
    public MatchImpl(Map map, int id) {
        this.id = id;
        this.map = map;
        buyPeriod = true;
        teams.add(new TeamImpl(this, Team.Side.ATTACKER));
        teams.add(new TeamImpl(this, Team.Side.DEFENDER));
        modules.add(new Shop(this));
        modules.add(new AgentPicker(this));
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
        if (attacker == defender) return;
        attacker.setSide(Team.Side.DEFENDER);
        defender.setSide(Team.Side.ATTACKER);
        callEvent(new SwitchingSidesEvent(this));
    }

    @Override
    public void endRound() {
        callEvent(new RoundFinishEvent(this));
        Countdown countdown = getModule(Countdown.class);
        if (countdown != null) {
            countdown.stop();
        }
    }

    @Override
    public void nextRound() {
        Countdown countdown = getModule(Countdown.class);
        if (countdown != null) modules.remove(countdown);
        final Countdown c = new Countdown(Countdown.Context.ROUND_ENDING);
        modules.add(c);
        for (Participant participant : getPlayers().values()) {
            c.getBossBar().addPlayer(participant.getPlayer());
        }
        c.start();
        c.onComplete(() -> {
            rounds.add(new RoundImpl(rounds.size() + 1));
            if (rounds.size() / 7 == 1) switchSides();
            buyPeriod = true;
            modules.remove(c);
            final Countdown cc = new Countdown(Countdown.Context.ROUND_STARTING);
            modules.add(cc);
            for (Participant participant : getPlayers().values()) {
                cc.getBossBar().addPlayer(participant.getPlayer());
                if (participant.isDead()) {
                    if (participant.getPlayer().isDead()) {
                        participant.getPlayer().spigot().respawn();
                    }
                    participant.applyDefaultSet();
                }
                participant.toTeam();
            }
            cc.onComplete(() -> {
                callEvent(new RoundStartEvent(this));
                buyPeriod = false;
                modules.remove(cc);
                final Countdown ccc = new Countdown(Countdown.Context.BEFORE_SPIKE);
                modules.add(ccc);
                ccc.start();
            });
        });
    }

    @Override
    public boolean start() {
        if (getPlayers().size() < 10) return false;
        MatchStartEvent e = new MatchStartEvent(this);
        callEvent(e);
        if (!e.isCancelled()) {
            AgentPicker agentPicker = getModule(AgentPicker.class);
            if (agentPicker == null) return false;
            agentPicker.close();
            modules.remove(agentPicker);
            for (Participant p : getPlayers().values()) {
                p.applyDefaultSet();
            }
            return true;
        }
        end(MatchTerminateEvent.Reason.COMPLETE);
        return false;
    }

    @Override
    public MatchResult end(MatchTerminateEvent.Reason reason) {
        map.setUse(false);
        if (!isComplete()) {
            callEvent(new MatchTerminateEvent(this, reason));
            return null;
        }
        MatchResult result = new MatchResult(this);
        Location loc = MatchManager.getInstance().getLobby();
        for (Participant p : getPlayers().values()) {
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
    public void joinTeam(Team.Side side, Player player) {
        Team team = teams.stream().filter(t -> t.getSide().equals(side)).findFirst().orElse(null);
        if (team == null) return;
        Participant participant = new ParticipantImpl(player, team);
        team.getMembers().add(participant);
        inventories.put(participant, player.getInventory().getContents().clone());
        player.getInventory().clear();
        Datastore.getInstance().loadPlayer(participant);
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
    public <N extends Module> N getModule(Class<? extends N> key) {
        return (N) modules.stream().filter(m -> m.getClass() == key).findFirst().orElse(null);
    }
}
