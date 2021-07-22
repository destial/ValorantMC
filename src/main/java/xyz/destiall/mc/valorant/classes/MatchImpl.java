package xyz.destiall.mc.valorant.classes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.destiall.mc.valorant.api.AgentPicker;
import xyz.destiall.mc.valorant.api.Map;
import xyz.destiall.mc.valorant.api.Match;
import xyz.destiall.mc.valorant.api.Participant;
import xyz.destiall.mc.valorant.api.Shop;
import xyz.destiall.mc.valorant.api.Team;
import xyz.destiall.mc.valorant.api.events.match.MatchCompleteEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchStartEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchTerminateEvent;
import xyz.destiall.mc.valorant.api.events.match.SwitchingSidesEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundFinishEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundStartEvent;
import xyz.destiall.mc.valorant.managers.MatchManager;
import xyz.destiall.mc.valorant.utils.Countdown;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MatchImpl implements Match {
    private final Set<Team> teams = new HashSet<>();
    private final HashMap<Participant, ItemStack[]> inventories = new HashMap<>();
    private final Map map;
    private final Shop shop;
    private final int id;
    private Countdown countdown;
    private int round;
    private boolean buyPeriod;
    private AgentPicker agentPicker;
    public MatchImpl(Map map, int id) {
        this.id = id;
        this.map = map;
        this.shop = new Shop(this);
        teams.add(new TeamImpl(this, Team.Side.ATTACKER));
        teams.add(new TeamImpl(this, Team.Side.DEFENDER));
        round = 0;
        buyPeriod = true;
        this.agentPicker = new AgentPicker(this);
        this.countdown = null;
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
    public Integer getRound() {
        return round;
    }

    @Override
    public Map getMap() {
        return map;
    }

    @Override
    public Shop getShop() {
        return shop;
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public boolean isBuyPeriod() {
        return buyPeriod;
    }

    @Override
    public boolean isWaitingForPlayers() {
        return agentPicker != null;
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
        if (countdown != null) {
            countdown.stop();
        }
    }

    @Override
    public void nextRound() {
        countdown = new Countdown(Countdown.Context.ROUND_ENDING);
        for (Participant participant : getPlayers().values()) {
            countdown.getBossBar().addPlayer(participant.getPlayer());
        }
        countdown.onComplete(() -> {
            round++;
            if (round / 7 == 1) switchSides();
            buyPeriod = true;
            countdown = new Countdown(Countdown.Context.ROUND_STARTING);
            for (Participant participant : getPlayers().values()) {
                countdown.getBossBar().addPlayer(participant.getPlayer());
                if (participant.isDead()) {
                    if (participant.getPlayer().isDead()) {
                        participant.getPlayer().spigot().respawn();
                    }
                    participant.applyDefaultSet();
                }
                participant.toTeam();
            }
            countdown.onComplete(() -> {
                callEvent(new RoundStartEvent(this));
                buyPeriod = false;
            });
        });
    }

    @Override
    public boolean start() {
        if (getPlayers().size() < 10) return false;
        MatchStartEvent e = new MatchStartEvent(this);
        callEvent(e);
        if (!e.isCancelled()) {
            agentPicker.close();
            agentPicker = null;
            for (Participant p : getPlayers().values()) {
                p.applyDefaultSet();
            }
            return true;
        }
        end(MatchTerminateEvent.Reason.COMPLETE);
        return false;
    }

    @Override
    public void end(MatchTerminateEvent.Reason reason) {
        map.setUse(false);
        Location loc = MatchManager.getInstance().getLobby();
        for (Participant p : getPlayers().values()) {
            p.getPlayer().getInventory().clear();
            ItemStack[] stacks = inventories.get(p);
            p.getPlayer().getInventory().addItem(stacks);
            p.getPlayer().teleport(loc);
        }
        inventories.clear();
        teams.clear();
        buyPeriod = false;
        if (isComplete()) {
            callEvent(new MatchCompleteEvent(this));
            return;
        }
        callEvent(new MatchTerminateEvent(this, reason));
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
    }

    @Override
    public void setCountdown(Countdown countdown) {
        this.countdown = countdown;
    }
}
