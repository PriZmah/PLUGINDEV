package com.gmail.prizmahdiep.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.prizmahdiep.events.FFAPlayerLoadEvent;
import com.gmail.prizmahdiep.events.FFAPlayerUnloadEvent;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.PlayerKit;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class FFAPlayersManager 
{
    public static Map<UUID, FFAPlayer> ffa_players;

    public FFAPlayersManager()
    {
        ffa_players = new HashMap<>();
    }

    public boolean isOnFFA(UUID p)
    {
        return ffa_players.containsKey(p);   
    }

    public boolean addPlayerToFFA(Player p, PlayerKit k, SpawnLocation s)
    {  
        UUID piud = p.getUniqueId();
        FFAPlayer pf = new FFAPlayer(p, k, s);
        if (isOnFFA(piud)) return false;
        else ffa_players.put(p.getUniqueId(), pf);

        Bukkit.getServer().getPluginManager().callEvent(new FFAPlayerLoadEvent(pf));
        return true;
    }

    public boolean removePlayerFromFFA(Player p)
    {
        UUID piud = p.getUniqueId();
        if (isOnFFA(piud)) 
        {
            Bukkit.getServer().getPluginManager().callEvent(new FFAPlayerUnloadEvent(p));
            ffa_players.remove(piud);
        }
        else return false;
        return true;
    }
}
