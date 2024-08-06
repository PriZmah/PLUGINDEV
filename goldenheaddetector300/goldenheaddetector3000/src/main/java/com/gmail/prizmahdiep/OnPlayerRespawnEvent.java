package com.gmail.prizmahdiep;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.NamespacedKey;


public class OnPlayerRespawnEvent implements Listener
{
    @EventHandler
    public void onPlayerRespawn(PlayerInteractEvent event) 
    {
        NamespacedKey a = new NamespacedKey();
    }
}
