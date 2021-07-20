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
import xyz.destiall.mc.valorant.api.events.match.MatchInterruptEvent;
import xyz.destiall.mc.valorant.api.events.match.MatchStartEvent;
import xyz.destiall.mc.valorant.api.events.match.SwitchingSidesEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundFinishEvent;
import xyz.destiall.mc.valorant.api.events.round.RoundStartEvent;
import xyz.destiall.mc.valorant.managers.MatchManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MatchImpl implements Match {
    private final Set<Team> teams = new HashSet<>();
    private final HashMap<Participant, ItemStack[]> inventories = new HashMap<>();
    private final Map map;
    private final Shop shop;
    private final int id;
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
        for (Team team : teams) {
            for (Participant p : team.getMembers()) {
                p.applyDefaultSet();
            }
        }
        callEvent(new SwitchingSidesEvent(this));
    }

    @Override
    public void endRound() {

    }

    @Override
    public void nextRound() {
        callEvent(new RoundFinishEvent(this));
        round++;
        callEvent(new RoundStartEvent(this));
        buyPeriod = true;
    }

    @Override
    public void start() {
        MatchStartEvent e = new MatchStartEvent(this);
        callEvent(e);
        if (!e.isCancelled()) {
            agentPicker.close();
            agentPicker = null;
            for (Participant p : getPlayers().values()) {
                p.applyDefaultSet();
            }
            return;
        }
        end();
    }

    @Override
    public void end() {
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
        callEvent(new MatchInterruptEvent(this));
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
}
