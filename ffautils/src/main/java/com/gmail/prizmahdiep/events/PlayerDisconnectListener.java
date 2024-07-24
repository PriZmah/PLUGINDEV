package com.gmail.prizmahdiep.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.SpawnUtils;

public class PlayerDisconnectListener implements Listener
{
    private FFAPlayersHandler fph;
    private SpawnUtils sup;

    public PlayerDisconnectListener(FFAPlayersHandler fph, SpawnUtils sup)
    {
        this.fph = fph;
        this.sup = sup;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        unloadFromFFA(p);
    }

    public void unloadFromFFA(Player p)
    {
        if (!fph.isOnFFA(p.getUniqueId())) return;

        FFAPlayer pf = FFAPlayersHandler.ffa_players.get(p.getUniqueId());
        SpawnLocation main_spawn = sup.getMainSpawn(); 
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
