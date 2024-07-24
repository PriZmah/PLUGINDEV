package com.gmail.prizmahdiep.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class PvPUtils 
{
    public boolean healPlayer(Player p)    
    {
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        return true;
    }
}
