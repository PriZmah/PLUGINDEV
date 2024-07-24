package com.gmail.prizmahdiep.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.prizmahdiep.events.custom.FFAPlayerLoadEvent;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.utils.KitUtils;
import com.gmail.prizmahdiep.utils.SpawnUtils;

public class FFAPlayerLoadListener implements Listener
{
    private KitUtils ku;
    private SpawnUtils su;

    public FFAPlayerLoadListener(KitUtils ku, SpawnUtils su)
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
