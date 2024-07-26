package com.gmail.prizmahdiep.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.prizmahdiep.events.custom.FFAPlayerLoadEvent;
import com.gmail.prizmahdiep.handlers.KitHandler;
import com.gmail.prizmahdiep.handlers.SpawnHandler;
import com.gmail.prizmahdiep.objects.FFAPlayer;

public class FFAPlayerLoadListener implements Listener
{
    private KitHandler ku;
    private SpawnHandler su;

    public FFAPlayerLoadListener(KitHandler ku, SpawnHandler su)
    {
        this.su = su;
        this.ku = ku;
    }
    @EventHandler
    public void onFFAPlayerLoad(FFAPlayerLoadEvent event) 
    {
        FFAPlayer fp = event.getFFAPlayer();
        Player p = fp.getPlayer();
        ku.setPlayerKit(fp.getLastPlayerKit().getName(), p);
        su.teleportEntityToSpawn(fp.getPlayerChosenSpawn().getName(), p);
    }
}
