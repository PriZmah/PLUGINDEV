package com.gmail.prizmahdiep.managers;

import java.util.Map;
import java.util.Set;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.gmail.prizmahdiep.database.SpawnDatabase;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class SpawnManager
{
    public static Map<String, SpawnLocation> spawns;
    private SpawnDatabase spawn_database;
    
    public SpawnManager(SpawnDatabase spawn_database)
    {
        this.spawn_database = spawn_database;
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
        try 
        {
            spawn_database.addSpawn(new SpawnLocation(name, loc, type));
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return true;
    }

    public boolean removeSpawn(String name)
    {
        if (spawns.containsKey(name)) spawns.remove(name);
        try 
        {
            spawn_database.removeSpawn(name);
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
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
        try 
        {
            spawns = spawn_database.getSpawns();
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
    
        return spawns.size();
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
