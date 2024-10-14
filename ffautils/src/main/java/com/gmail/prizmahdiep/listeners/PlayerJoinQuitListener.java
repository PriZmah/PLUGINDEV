package com.gmail.prizmahdiep.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.prizmahdiep.managers.CombatTagManager;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.SpawnManager;

public class PlayerJoinQuitListener implements Listener 
{
    private FFAPlayersManager fph;
    private CombatTagManager ctm;
    private KitEditorManager kem;

    public PlayerJoinQuitListener(SpawnManager sup, FFAPlayersManager fph, CombatTagManager ctm, KitEditorManager kem)
    {
        this.fph = fph;
        this.ctm = ctm;
        this.kem = kem;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) 
    {
        
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        clean(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e)
    {
        clean(e.getPlayer().getUniqueId());
    }

    private void clean(UUID p)
    {
        if (kem.getKitEditorPlayers().get(p) != null)
            kem.unload_player(p);

        if (fph.isOnFFA(p))
        {
            fph.removePlayerFromFFA(fph.getFFAPlayers().get(p));
            if (ctm.isTagged(p))
                Bukkit.getPlayer(p).setHealth(0);
        }
        
        if (fph.isIdle(p))
            fph.removePlayerFromIdle(p);

    }
}
