package com.gmail.prizmahdiep.objects;

import org.bukkit.Location;

public class SpawnLocation 
{
    public static final String SPAWN = "spawn";
    public static final String STANDARD = "standard";
    public static final String FTN = "ft#";
    public static final String EDITOR_ROOM = "editor";

    private final String name;
    private final Location location;
    private String type;

    public SpawnLocation(String name, Location location, String type)
    {
        this.name = name;
        this.location = location;
        this.type = type;
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

    public boolean equals(SpawnLocation other)
    {
        return this.name.equals(other.getName());
    }
}
