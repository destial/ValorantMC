package xyz.destiall.mc.valorant.api.sidebar;

import xyz.destiall.mc.valorant.api.items.Team;
import xyz.destiall.mc.valorant.api.player.VPlayer;

public abstract class Sidebar {
    protected Team team;
    public Sidebar(Team team) {
        this.team = team;
    }
    public abstract void create();
    public abstract void rejoin(VPlayer player);
    public abstract void invalidate(VPlayer player);
    public abstract void destroy();
    public abstract void render();
}
