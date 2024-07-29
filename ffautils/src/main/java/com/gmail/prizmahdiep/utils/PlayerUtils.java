package com.gmail.prizmahdiep.utils;

import org.bukkit.entity.Player;

public class PlayerUtils 
{
    public static void resetPlayerStatus(Player p)
    {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(0);
    }
}
