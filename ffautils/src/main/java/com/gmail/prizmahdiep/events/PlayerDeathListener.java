package com.gmail.prizmahdiep.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.handlers.SpawnHandler;

public class PlayerDeathListener implements Listener
{
    public PlayerDeathListener(FFAPlayersHandler fph, SpawnHandler su) 
    {
        
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev)
    {
    
    }
}
