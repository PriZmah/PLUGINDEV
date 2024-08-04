package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.PlayerUtils;

public class PlayerDisconnectListener implements Listener
{
    private FFAPlayersManager fph;
    private SpawnManager sup;


    public PlayerDisconnectListener(FFAPlayersManager fph, SpawnManager sup)
    {
        this.fph = fph;
        this.sup = sup;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        
        removeFromFFA(p);
    }

    private void removeFromFFA(Player p)
    {
        if (!(fph.isOnFFA(p.getUniqueId()) || fph.isIdle(p.getUniqueId())))
        {
            if (!p.hasPermission("ffautils.admin"))
            {
                PlayerUtils.resetPlayerStatus(p);
                sup.teleportEntityToSpawn(sup.getMainSpawn().getName(), p);
                p.getInventory().clear();
                p.clearActivePotionEffects();
            }
            return;
        }

        FFAPlayer pf = FFAPlayersManager.ffa_players.get(p.getUniqueId());
        if (pf != null)
        {
            SpawnLocation main_spawn = sup.getMainSpawn(); 
            pf.setPlayerKit(null);
        
            if (main_spawn != null)
                pf.setPlayerSpawn(main_spawn);
        

            fph.removePlayerFromFFA(pf);
        }
        fph.removePlayerFromIdle(pf);
    }
}
