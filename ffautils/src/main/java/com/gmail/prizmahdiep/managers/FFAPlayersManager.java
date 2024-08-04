package com.gmail.prizmahdiep.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.prizmahdiep.events.FFAPlayerLoadEvent;
import com.gmail.prizmahdiep.events.FFAPlayerUnloadEvent;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.KitInterface;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class FFAPlayersManager 
{
    public static Map<UUID, FFAPlayer> ffa_players;
    public static Map<UUID, FFAPlayer> idle_ffa_players;

    public FFAPlayersManager()
    {
        ffa_players = new HashMap<>();
        idle_ffa_players = new HashMap<>();
    }

    public boolean isOnFFA(UUID p)
    {
        return ffa_players.containsKey(p);   
    }

    public boolean isIdle(UUID p)
    {
        return idle_ffa_players.containsKey(p);
    }

    public boolean addPlayerToFFA(Player p, KitInterface k, SpawnLocation s)
    {  
        UUID piud = p.getUniqueId();
        if (isOnFFA(piud)) return false;
        
        FFAPlayer pf = new FFAPlayer(p, k, s);
        ffa_players.put(p.getUniqueId(), pf);

        Bukkit.getServer().getPluginManager().callEvent(new FFAPlayerLoadEvent(pf));
        return true;
    }

    public boolean movePlayerFromFFA(FFAPlayer p)
    {
        UUID piud = p.getUUID();
        idle_ffa_players.put(piud, p);
        removePlayerFromFFA(p);
        return true;
    }

    public boolean movePlayerFromIdle(FFAPlayer p)
    {
        UUID piud = p.getUUID();
        if (!isIdle(piud)) return false;
        p.setPlayerKit(p.getLastPlayerKit());
        p.setPlayerSpawn(p.getLastChosenSpawn());

        ffa_players.put(piud, p);
        removePlayerFromIdle(p);
        Bukkit.getServer().getPluginManager().callEvent(new FFAPlayerLoadEvent(p));
        return true;
    }

    public boolean removePlayerFromFFA(FFAPlayer p)
    {
        UUID piud = p.getUUID();
        if (isOnFFA(piud)) 
        {
            Bukkit.getServer().getPluginManager().callEvent(new FFAPlayerUnloadEvent(p));
            ffa_players.remove(piud);
        }
        else return false;
        return true;
    }

    public void removePlayerFromIdle(FFAPlayer p) 
    {
        UUID piud = p.getUUID();
        if (isIdle(piud))
            idle_ffa_players.remove(piud);
    }
}
