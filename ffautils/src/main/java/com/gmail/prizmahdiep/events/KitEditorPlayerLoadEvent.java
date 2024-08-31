package com.gmail.prizmahdiep.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.prizmahdiep.objects.Kit;

public class KitEditorPlayerLoadEvent extends Event implements Cancellable {
    private boolean isCancelled;
    private Player p;
    private Kit editable_kit;
    private static final HandlerList HANDLERS = new HandlerList();

    
    public KitEditorPlayerLoadEvent(Player p, Kit editable_kit_name) 
    {
        this.isCancelled = false;
        this.p = p;
        this.editable_kit = editable_kit_name;
    }
    
    public static HandlerList getHandlerList() 
    {
        return HANDLERS;
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

    public Kit getEditableKit() 
    {
        return this.editable_kit;
    }
}
