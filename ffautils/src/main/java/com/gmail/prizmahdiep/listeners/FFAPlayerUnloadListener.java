package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.prizmahdiep.events.FFAPlayerUnloadEvent;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;

public class FFAPlayerUnloadListener implements Listener
{
    private SpawnManager su;

    public FFAPlayerUnloadListener(SpawnManager su)
    {
        this.su = su;
    }

    @EventHandler
    public void onPlayerUnload(FFAPlayerUnloadEvent p)
    {
        Player pj = p.getPlayer();
        FFAPlayer pf = FFAPlayersManager.ffa_players.get(pj.getUniqueId());
        pf.getPlayer().getInventory().clear();
        pf.getPlayer().clearActivePotionEffects();
        su.teleportEntityToSpawn(pf.getPlayerChosenSpawn().getName(), pj);
    }
}
