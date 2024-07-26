package com.gmail.prizmahdiep.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.handlers.SpawnHandler;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class PlayerRespawnListener implements Listener
{
    private FFAPlayersHandler fph;
    private SpawnHandler su;
    private FFAUtils futils;

    public PlayerRespawnListener(FFAPlayersHandler fph, SpawnHandler su, FFAUtils pl) 
    {
        this.fph = fph;
        this.su = su;
        this.futils = pl;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent ev)
    {   
        Player p = ev.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() 
            {
                unloadPlayer(p);
            }
        }.runTaskLater(futils, 1);
    }

    private void unloadPlayer(Player p) 
    {
        if (!fph.isOnFFA(p.getUniqueId())) return;

        FFAPlayer pf = FFAPlayersHandler.ffa_players.get(p.getUniqueId());
        SpawnLocation main_spawn = su.getMainSpawn(); 
        pf.setPlayerKit(null);
        pf.setPlayerKit(null);
        
        if (main_spawn != null)
        {
            pf.setPlayerSpawn(main_spawn);
            pf.setPlayerSpawn(main_spawn);
        }

        fph.removePlayerFromFFA(p);
    }
}