package com.gmail.prizmahdiep.objects;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public class SerializableSelector 
{
    private Map<String, Integer> spawns; 
    private String default_spawn;
    private String kit;
    private String display_name;
    private String container_name;
    private String entity_type;
    private String location;
    private int id;

    public SerializableSelector(Selector selector)
    {
        this.spawns = selector.getSpawns();
        this.default_spawn = selector.getDefaultSpawn();
        this.kit = selector.getKit();
        this.display_name = selector.getDisplayName();
        this.container_name = selector.getContainerName();
        this.entity_type = selector.getEntity().getType().toString();
        this.id = selector.getID();
        Location l = selector.getLocation();
        String loc = 
        l.getX() + ";" + 
        l.getY() + ";" + 
        l.getZ() + ";" +
        l.getPitch() + ";" +
        l.getYaw() + ";" +
        l.getWorld().getName();
        this.location = loc;
    }

    public Selector getSelector()
    {
        double x, y, z;
        float pitch, yaw;
        World wrld;

        String[] str = location.split(";");
        x = Double.valueOf(str[0]);
        y = Double.valueOf(str[1]);
        z = Double.valueOf(str[2]);
        pitch = Float.valueOf(str[3]);
        yaw = Float.valueOf(str[4]);
        wrld = Bukkit.getWorld(str[5]);
        Location loc = new Location(wrld, x, y, z, yaw, pitch);

        return new Selector
        (
            loc, EntityType.fromName(entity_type), default_spawn, kit, display_name, container_name, id, spawns
        );
    }
}
