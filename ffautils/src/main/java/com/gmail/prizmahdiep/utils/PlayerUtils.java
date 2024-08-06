package com.gmail.prizmahdiep.utils;

import org.bukkit.entity.Player;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class PlayerUtils 
{
    private static FFAPlayersManager fph;

    public PlayerUtils (FFAPlayersManager fpa)
    {
        fph = fpa;
    }

    public static void resetPlayerStatus(Player p)
    {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(0);
    }

    public static void resetPlayer(Player p)
    {
        SpawnLocation a = SpawnManager.mainSpawn();
        if (SpawnLocationUtil.isValidSpawn(a))
            p.teleport(a.getLocation());
        p.getInventory().clear();
        p.clearActivePotionEffects();
        resetPlayerStatus(p);
    }

    public static void resetFFAPlayer(FFAPlayer p)
    {
        p.setPlayerSpawn(SpawnManager.mainSpawn());
        p.setPlayerKit(null);
        resetPlayerStatus(p.getPlayer());
        fph.movePlayerFromFFA(p);
    }

    public static void clearFFAPlayer(FFAPlayer p)
    {
        resetFFAPlayer(p);
        fph.removePlayerFromIdle(p);
    }
}
