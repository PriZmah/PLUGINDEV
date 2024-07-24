package com.gmail.prizmahdiep.utils;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.config.SpawnsConfig;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class SpawnUtils
{
    public static Map<String, SpawnLocation> spawns;
    private FFAUtils plugin;
    private SpawnsConfig spawn_cfg;
    
    public SpawnUtils(FFAUtils pl, SpawnsConfig scfg)
    {
        this.plugin = pl;
        this.spawn_cfg = scfg;
        spawns = new HashMap<>();
        reloadSpawns();
    }
    
    
    public Set<String> getExistingSpawns()
    {
        return spawns.keySet();
    }

    public boolean createSpawn(String name, Location loc, String type)
    {
        if (spawns.containsKey(name)) return false;

        SpawnLocation m = new SpawnLocation(name, loc, type);
        if (!spawn_cfg.createSpawnConfigEntry(m)) return false;

        spawns.put(name, m);
        return true;
    }

    public boolean removeSpawn(String name)
    {
        if (!spawn_cfg.removeSpawnConfigEntry(name)) return false;
        spawns.remove(name);
        return true;
    }

    public boolean teleportEntityToSpawn(String name, Entity p)
    {
        if (!spawns.containsKey(name)) return false;
        p.teleport(spawns.get(name).getLocation());
        return true;
    }
    
    public int reloadSpawns() 
    {
        int loaded_spawns = 0;
        spawns.clear();
        spawns.putAll(spawn_cfg.getSpawnObjects());
        
        loaded_spawns = spawns.size();
        plugin.getLogger().info(loaded_spawns + " spawns loaded");
        return loaded_spawns;
    }

    public SpawnLocation getMainSpawn()
    {
        for (SpawnLocation i : spawns.values())
        {
            if (i.getType().equals(SpawnLocation.SPAWN))
                return i;
        }
        return null;
    }
}
