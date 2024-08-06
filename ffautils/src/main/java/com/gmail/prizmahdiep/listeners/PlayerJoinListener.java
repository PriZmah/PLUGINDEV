package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.utils.PlayerUtils;

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

    private void teleportToMainSpawn(Player p) 
    {
        if (!(fph.isOnFFA(p.getUniqueId()) || fph.isIdle(p.getUniqueId())))
        {
            if (!p.hasPermission("ffautils.admin"))
            {
                PlayerUtils.resetPlayerStatus(p);
                sup.teleportEntityToSpawn(SpawnManager.mainSpawn().getName(), p);
                p.getInventory().clear();
                p.clearActivePotionEffects();
            }
            return;
        }
    }


}
