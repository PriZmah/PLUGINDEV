package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;


public class PlayerRespawnListener implements Listener
{
    private FFAPlayersManager fph;
    private FFAUtils futils;
    private SpawnManager sm;

    public PlayerRespawnListener(FFAPlayersManager fph, FFAUtils pl, SpawnManager sm) 
    {
        this.fph = fph;
        this.futils = pl;
        this.sm = sm;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent ev)
    {   

        if (FFAPlayersManager.ffa_players.get(ev.getPlayer().getUniqueId()) != null)
            new BukkitRunnable() {
                @Override
                public void run() 
                {
                    resetPlayer(ev.getPlayer());
                }
            }.runTaskLater(futils, 1);
    }

    private void resetPlayer(Player p) 
    {
        FFAPlayer pj = FFAPlayersManager.ffa_players.get(p.getUniqueId());
        pj.setPlayerKit(null);
        pj.setPlayerSpawn(sm.getMainSpawn());
        
        fph.movePlayerFromFFA(pj);
    }
}