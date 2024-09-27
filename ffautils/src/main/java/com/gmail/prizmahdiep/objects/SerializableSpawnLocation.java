package com.gmail.prizmahdiep.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class SerializableSpawnLocation 
{
    private String world_name, type, thumbnail, display_name, lore;
    private double x, y, z;
    private float pitch, yaw;

    public SerializableSpawnLocation(String world_name, String type, String thumbnail, String display_name, String lore, double x, double y, double z, float pitch, float yaw)
    {
        this.world_name = world_name;
        this.type = type;
        this.thumbnail = thumbnail;
        this.display_name = display_name;
        this.lore = lore;
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
        this.thumbnail = l.getThumbnail().toString();
        this.display_name = l.getDisplayName();
        this.lore = l.getLore();
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
        SpawnLocation sl = new SpawnLocation(name, l, this.type, this.display_name, this.lore, Material.getMaterial(thumbnail));
        return sl;
    }
}
