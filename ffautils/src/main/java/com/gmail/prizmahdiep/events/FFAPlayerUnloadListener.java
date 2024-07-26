package com.gmail.prizmahdiep.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.prizmahdiep.events.custom.FFAPlayerUnloadEvent;
import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.handlers.SpawnHandler;
import com.gmail.prizmahdiep.objects.FFAPlayer;

public class FFAPlayerUnloadListener implements Listener
{
    private SpawnHandler su;

    public FFAPlayerUnloadListener(SpawnHandler su)
    {
        this.su = su;
    }

    @EventHandler
    public void onPlayerUnload(FFAPlayerUnloadEvent p)
    {
        Player pj = p.getPlayer();
        FFAPlayer pf = FFAPlayersHandler.ffa_players.get(pj.getUniqueId());
        pf.getPlayer().getInventory().clear();
        pf.getPlayer().clearActivePotionEffects();
        su.teleportEntityToSpawn(pf.getPlayerChosenSpawn().getName(), pj);
    }
}
