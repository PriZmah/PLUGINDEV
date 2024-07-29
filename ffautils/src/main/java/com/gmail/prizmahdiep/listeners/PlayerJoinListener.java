package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;

public class PlayerJoinListener implements Listener 
{
    private SpawnManager sup;
    private FFAPlayersManager fph;

    public PlayerJoinListener(SpawnManager sup, FFAPlayersManager fph)
    {
        this.sup = sup;
        this.fph = fph;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) 
    {
        teleportToMainSpawn(event.getPlayer());
    }

    private void teleportToMainSpawn(Player player) 
    {
        if (sup.getMainSpawn() == null || !fph.isOnFFA(player.getUniqueId())) return;
        sup.teleportEntityToSpawn(sup.getMainSpawn().getName(), player);
    }


}
