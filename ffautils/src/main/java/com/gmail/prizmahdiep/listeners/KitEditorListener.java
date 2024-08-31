package com.gmail.prizmahdiep.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.prizmahdiep.events.KitEditorPlayerLoadEvent;
import com.gmail.prizmahdiep.events.KitEditorPlayerUnloadEvent;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.PlayerUtils;

public class KitEditorListener implements Listener
{
    private SpawnManager sm;
    //private KitManager km;

    public KitEditorListener(SpawnManager sm, KitManager km)
    {
        this.sm = sm;
        //this.km = km;
    }

    @EventHandler
    public void onKitEditorStart(KitEditorPlayerLoadEvent ev)
    {
        PlayerUtils.teleportPlayerToSpawn(ev.getPlayer(), sm.getSpawnOfType(SpawnLocation.EDITOR_ROOM));
        PlayerUtils.setPlayerKit(ev.getPlayer(), ev.getEditableKit());
    } 
    
    @EventHandler
    public void onKitEditorEnd(KitEditorPlayerUnloadEvent ev)
    {
        PlayerUtils.resetPlayer(ev.getPlayer());
    }
}
