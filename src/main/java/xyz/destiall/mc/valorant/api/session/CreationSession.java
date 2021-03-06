package xyz.destiall.mc.valorant.api.session;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import xyz.destiall.mc.valorant.api.map.Site;
import xyz.destiall.mc.valorant.factories.MapMatchFactory;

import java.util.HashSet;
import java.util.Set;

public class CreationSession {
    public static final Set<CreationSession> ACTIVE_SESSIONS = new HashSet<>();
    public static CreationSession getSession(Player player) {
        return ACTIVE_SESSIONS.stream().filter(s -> s.player.getUniqueId().equals(player.getUniqueId())).findFirst().orElse(null);
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
        this.world = world;
        bounds = boundingBox;
        name = mapName;
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

    public boolean addWall(BoundingBox wall) {
        if (walls.stream().anyMatch(b -> b.contains(wall))) return false;
        walls.add(wall);
        return true;
    }

    public Set<Site> getSites() {
        return sites;
    }

    public boolean addSite(Site site) {
        if (sites.stream().anyMatch(s -> s.getSiteType() == site.getSiteType())) return false;
        sites.add(site);
        return true;
    }

    public void setAttackerSpawn(Location attackerSpawn) {
        this.attackerSpawn = attackerSpawn;
    }

    public void setDefenderSpawn(Location defenderSpawn) {
        this.defenderSpawn = defenderSpawn;
    }

    public boolean finish() {
        if (MapMatchFactory.createMap(this) != null) {
            ACTIVE_SESSIONS.remove(this);
            return true;
        }
        return false;
    }
}
