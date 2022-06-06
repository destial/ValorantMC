package xyz.destiall.mc.valorant.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class VEvent extends Event {
    public static final HandlerList handlerList = new HandlerList();@Override
    public HandlerList getHandlers() {return handlerList;}
    public static HandlerList getHandlerList() {return handlerList;}
}
