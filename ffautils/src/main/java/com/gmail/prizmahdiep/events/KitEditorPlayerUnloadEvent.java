package com.gmail.prizmahdiep.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KitEditorPlayerUnloadEvent extends Event implements Cancellable
{
    private boolean cancelled = false;
    private Player player;
    private static final HandlerList HANDLERS = new HandlerList();

    public KitEditorPlayerUnloadEvent(Player player) 
    {
        this.cancelled = false;
        this.player = player;
    }

    @Override
    public boolean isCancelled() 
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean arg0) 
    {
        this.cancelled = arg0;
    }

    @Override
    public @NotNull HandlerList getHandlers() 
    {
        return HANDLERS;
    }
    
    public Player getPlayer()
    {
        return this.player;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
