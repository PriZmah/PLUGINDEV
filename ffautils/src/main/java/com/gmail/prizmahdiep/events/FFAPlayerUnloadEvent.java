package com.gmail.prizmahdiep.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.prizmahdiep.objects.FFAPlayer;

import org.bukkit.event.Cancellable;

public class FFAPlayerUnloadEvent extends Event implements Cancellable
{
    private boolean isCancelled;
    private FFAPlayer p;
    private static final HandlerList HANDLERS = new HandlerList();

    
    public FFAPlayerUnloadEvent(FFAPlayer p) 
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

    public FFAPlayer getFFAPlayer()
    {
        return this.p;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}