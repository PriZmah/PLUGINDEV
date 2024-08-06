package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.utils.PlayerUtils;


public class PlayerRespawnListener implements Listener
{
    private FFAUtils futils;

    public PlayerRespawnListener(FFAUtils pl) 
    {
        this.futils = pl;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent ev)
    {   

        if (FFAPlayersManager.ffa_players.get(ev.getPlayer().getUniqueId()) != null)
            new BukkitRunnable() {
                @Override
                public void run() 
                {
                    resetPlayer(ev.getPlayer());
                }
            }.runTaskLater(futils, 1);
    }

    private void resetPlayer(Player p) 
    {
        FFAPlayer pj = FFAPlayersManager.ffa_players.get(p.getUniqueId());
        PlayerUtils.resetFFAPlayer(pj);
    }
}