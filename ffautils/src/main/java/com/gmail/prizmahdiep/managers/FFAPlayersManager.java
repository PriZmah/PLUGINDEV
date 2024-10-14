package com.gmail.prizmahdiep.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.prizmahdiep.events.FFAPlayerLoadEvent;
import com.gmail.prizmahdiep.events.FFAPlayerUnloadEvent;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class FFAPlayersManager 
{
    private Map<UUID, FFAPlayer> ffa_players;
    private Map<UUID, FFAPlayer> idle_ffa_players;

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

    public void addPlayerToFFA(Player p, Kit k, SpawnLocation s)
    {  
        UUID piud = p.getUniqueId();
        removePlayerFromIdle(piud);
        
        FFAPlayer pf = idle_ffa_players.get(piud);
        if (pf == null)
            pf = new FFAPlayer(p, k.getName(), s.getName());
        else
        {
            pf.setLastPlayerKitName(pf.getCurrentPlayerKitName());
            pf.setCurrentPlayerKitName(k.getName());
            pf.setLastSpawnName(pf.getLastSpawnName());
            pf.setCurrentSpawnName(s.getName());
            movePlayerFromIdle(pf);
        }
        ffa_players.put(p.getUniqueId(), pf);

        Bukkit.getServer().getPluginManager().callEvent(new FFAPlayerLoadEvent(pf));
    }

    public void movePlayerFromFFA(FFAPlayer p)
    {
        UUID piud = p.getUUID();
        idle_ffa_players.put(piud, p);
        removePlayerFromFFA(p);
    }

    private void movePlayerFromIdle(FFAPlayer p)
    {
        UUID piud = p.getUUID();
        ffa_players.put(piud, p);
        removePlayerFromIdle(piud);
        Bukkit.getServer().getPluginManager().callEvent(new FFAPlayerLoadEvent(p));
    }

    public void removePlayerFromFFA(FFAPlayer p)
    {
        UUID piud = p.getUUID();
        Bukkit.getServer().getPluginManager().callEvent(new FFAPlayerUnloadEvent(p));
        ffa_players.remove(piud);
    }

    public void removePlayerFromIdle(UUID p) 
    {
        idle_ffa_players.remove(p);
    }

    public void addPlayerToIdle(UUID p, FFAPlayer a)
    {
        if (!isIdle(p))
            idle_ffa_players.put(p, a);
    }

    public Map<UUID, FFAPlayer> getFFAPlayers()
    {
        return this.ffa_players;
    }

    public Map<UUID, FFAPlayer> getIdleFFAPlayers()
    {
        return this.idle_ffa_players;
    }
}
