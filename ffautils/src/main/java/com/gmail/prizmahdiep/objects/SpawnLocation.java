package com.gmail.prizmahdiep.objects;

import org.bukkit.Location;
import org.bukkit.Material;

public class SpawnLocation 
{
    public static final String SPAWN = "spawn";
    public static final String STANDARD = "standard";
    public static final String FTN = "ft#";
    public static final String EDITOR_ROOM = "editor";

    private final String name;
    private final Location location;
    private String type, lore, display_name;
    private Material spawn_thumbnail;

    public SpawnLocation(String name, Location location, String type, String dsn, String lore, Material tmb)
    {
        this.name = name;
        this.display_name = dsn;
        this.lore = lore;
        this.location = location;
        this.type = type;
        this.spawn_thumbnail = tmb;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDisplayName()
    {
        return this.display_name;
    }

    public String getLore()
    {
        return this.lore;
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

    public Material getThumbnail()
    {
        return this.spawn_thumbnail;
    }
}
