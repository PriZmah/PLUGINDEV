package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.prizmahdiep.events.FFAPlayerUnloadEvent;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.utils.PlayerUtils;

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
        FFAPlayer pf = FFAPlayersManager.ffa_players.get(p.getPlayer().getUniqueId());
        Player pi = pf.getPlayer();
        
        pi.getInventory().setContents(pf.getPlayerKit().getInventory());;
        pi.clearActivePotionEffects();
        pi.addPotionEffects(pf.getPlayerKit().getPotionEffects());
        su.teleportEntityToSpawn(pf.getChosenSpawn().getName(), pi);
        PlayerUtils.resetPlayerStatus(pi);
    }
}
