package com.gmail.prizmahdiep.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SerializableSpawnLocation 
{
    private String world_name, type;
    private double x, y, z;
    private float pitch, yaw;

    public SerializableSpawnLocation(String world_name, String type, double x, double y, double z, float pitch, float yaw)
    {
        this.world_name = world_name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public SerializableSpawnLocation(SpawnLocation l)
    {
        Location ll = l.getLocation();
        this.world_name = ll.getWorld().getName();
        this.type = l.getType();
        this.x = ll.getX();
        this.y = ll.getY();
        this.z = ll.getZ();
        this.pitch = ll.getPitch();
        this.yaw = ll.getYaw();
    }

    public String getWorldName()
    {
        return this.world_name;
    }

    public SpawnLocation getSpawn(String name)
    {
        Location l = new Location(Bukkit.getWorld(world_name), x, y, z, yaw, pitch);
        SpawnLocation sl = new SpawnLocation(name, l, this.type);
        return sl;
    }
}
