package com.gmail.prizmahdiep.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.gmail.prizmahdiep.handlers.SpawnHandler;

public class SpawnLocation 
{
    public static final String SPAWN = "spawn";
    public static final String STANDARD = "standard";

    private final String name;
    private final Location location;
    private String type;

    public SpawnLocation(String name, Location location, String type)
    {
        this.name = name;
        this.location = location;
        this.type = type;
        if (type != null)
        {
            if (type.equals(SPAWN))
            {
                SpawnHandler.spawns.forEach((spawn_name, spawn) -> 
                    {
                        if (spawn.getType().equals(SPAWN))
                        {
                            Bukkit.getServer().getLogger().warning("Tried to create more than one spawn locations of type SPAWN");
                            this.type = STANDARD;
                            return;
                        }
                    }
                );
            }
            if (!type.equals(STANDARD) && !type.equals(SPAWN))
            {
                Bukkit.getServer().getLogger().warning("Spawn type not valid");
                this.type = STANDARD;
            }
        }

    }

    public String getName()
    {
        return this.name;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public String getType() 
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
