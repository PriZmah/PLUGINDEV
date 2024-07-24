package com.gmail.prizmahdiep.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.utils.SpawnUtils;

public class PlayerJoinListener implements Listener 
{
    private SpawnUtils sup;
    private FFAPlayersHandler fph;

    public PlayerJoinListener(SpawnUtils sup, FFAPlayersHandler fph)
    {
        this.sup = sup;
        this.fph = fph;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) 
    {
        teleportToMainSpawn(event.getPlayer());
    }

    private void teleportToMainSpawn(Player player) {
        if (sup.getMainSpawn() == null || !fph.isOnFFA(player.getUniqueId())) return;

        sup.teleportEntityToSpawn(sup.getMainSpawn().getName(), player);
    }


}
