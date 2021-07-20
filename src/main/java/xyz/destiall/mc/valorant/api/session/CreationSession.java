package xyz.destiall.mc.valorant.api.session;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.Site;

import java.util.HashSet;
import java.util.Set;

public class CreationSession {
    public static final Set<CreationSession> ACTIVE_SESSIONS = new HashSet<>();
    public static CreationSession getSession(Player player) {
        return ACTIVE_SESSIONS.stream().filter(s -> s.player == player).findFirst().orElse(null);
    }
    private final Player player;
    private final BoundingBox bounds;
    private final String name;

    private final Set<Site> sites = new HashSet<>();
    private final World world;
    private final Set<BoundingBox> walls = new HashSet<>();
    private Location attackerSpawn;
    private Location defenderSpawn;
    public CreationSession(Player player, String mapName, BoundingBox boundingBox, World world) {
        this.player = player;
        this.bounds = boundingBox;
        this.name = mapName;
        this.world = world;
        ACTIVE_SESSIONS.add(this);
    }

    public String getMapName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public Location getAttackerSpawn() {
        return attackerSpawn;
    }

    public Location getDefenderSpawn() {
        return defenderSpawn;
    }

    public Set<BoundingBox> getWalls() {
        return walls;
    }

    public World getWorld() {
        return world;
    }

    public void addWall(BoundingBox wall) {
        this.walls.add(wall);
    }

    public Set<Site> getSites() {
        return sites;
    }

    public void addSite(Site site) {
        this.sites.add(site);
    }

    public void setAttackerSpawn(Location attackerSpawn) {
        this.attackerSpawn = attackerSpawn;
    }

    public void setDefenderSpawn(Location defenderSpawn) {
        this.defenderSpawn = defenderSpawn;
    }

    public void finish() {

    }
}
