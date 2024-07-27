package com.gmail.prizmahdiep.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;

public class PlayerDeathListener implements Listener
{
    public PlayerDeathListener(FFAPlayersManager fph, SpawnManager su) 
    {
        
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev)
    {
    
    }
}
