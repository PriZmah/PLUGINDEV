package com.gmail.prizmahdiep.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.SpawnUtils;

public class PlayerRespawnListener implements Listener
{
    private FFAPlayersHandler fph;
    private SpawnUtils su;

    public PlayerRespawnListener(FFAPlayersHandler fph, SpawnUtils su) 
    {
        this.fph = fph;
        this.su = su;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent ev)
    {
        unloadPlayer(ev.getPlayer());
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