package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.prizmahdiep.events.FFAPlayerLoadEvent;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;

public class FFAPlayerLoadListener implements Listener
{
    private KitManager ku;
    private SpawnManager su;

    public FFAPlayerLoadListener(KitManager ku, SpawnManager su)
    {
        this.su = su;
        this.ku = ku;
    }
    @EventHandler
    public void onFFAPlayerLoad(FFAPlayerLoadEvent event) 
    {
        FFAPlayer fp = event.getFFAPlayer();
        Player p = fp.getPlayer();
        ku.setPlayerKit(fp.getPlayerKit().getName(), p);
        su.teleportEntityToSpawn(fp.getChosenSpawn().getName(), p);
    }
}
