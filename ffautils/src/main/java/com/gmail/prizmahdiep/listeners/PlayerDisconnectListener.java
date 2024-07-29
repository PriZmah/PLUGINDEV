package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class PlayerDisconnectListener implements Listener
{
    private FFAPlayersManager fph;
    private SpawnManager sup;
    private FFAUtils futils;

    public PlayerDisconnectListener(FFAPlayersManager fph, SpawnManager sup, FFAUtils futils)
    {
        this.fph = fph;
        this.sup = sup;
        this.futils = futils;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        
        new BukkitRunnable() {
            @Override
            public void run() 
            {
                removeFromFFA(p);
            }
        }.runTaskLater(futils, 1);
    }

    private void removeFromFFA(Player p)
    {
        if (!fph.isOnFFA(p.getUniqueId())) return;

        FFAPlayer pf = FFAPlayersManager.ffa_players.get(p.getUniqueId());
        SpawnLocation main_spawn = sup.getMainSpawn(); 
        pf.setPlayerKit(null);
        pf.setPlayerKit(null);
        
        if (main_spawn != null)
        {
            pf.setPlayerSpawn(main_spawn);
            pf.setPlayerSpawn(main_spawn);
        }

        fph.removePlayerFromFFA(p);
        fph.removePlayerFromIdleFFA(p);
    }
}
