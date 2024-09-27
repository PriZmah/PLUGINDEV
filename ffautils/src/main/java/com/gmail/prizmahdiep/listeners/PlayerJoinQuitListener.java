package com.gmail.prizmahdiep.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.utils.PlayerUtils;

public class PlayerJoinQuitListener implements Listener 
{
    private FFAPlayersManager fph;

    public PlayerJoinQuitListener(SpawnManager sup, FFAPlayersManager fph)
    {
        this.fph = fph;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) 
    {

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        UUID p = e.getPlayer().getUniqueId();
        boolean reset_flag = false;
        if (fph.isOnFFA(p)) { fph.getFFAPlayers().remove(p); reset_flag = true; }
        if (fph.isIdle(p)) { fph.getIdleFFAPlayers().remove(p); reset_flag = true; }

        if (reset_flag) PlayerUtils.resetPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e)
    {
        UUID p = e.getPlayer().getUniqueId();
        boolean reset_flag = false;
        if (fph.isOnFFA(p)) { fph.getFFAPlayers().remove(p); reset_flag = true; }
        if (fph.isIdle(p)) { fph.getIdleFFAPlayers().remove(p); reset_flag = true; }

        if (reset_flag) PlayerUtils.resetPlayer(e.getPlayer());
    }
}
