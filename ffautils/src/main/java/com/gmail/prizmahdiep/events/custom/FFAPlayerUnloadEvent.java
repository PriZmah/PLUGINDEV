package com.gmail.prizmahdiep.events.custom;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class FFAPlayerUnloadEvent extends Event implements Cancellable
{
    private boolean isCancelled;
    private Player p;
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() 
    {
        return HANDLERS;
    }

    public FFAPlayerUnloadEvent(Player p) 
    {
        this.isCancelled = false;
        this.p = p;
    }

    @Override
    public HandlerList getHandlers() 
    {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Player getPlayer()
    {
        return this.p;
    }
}