package com.gmail.prizmahdiep.objects;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class Selector
{
    private static int id_count = 0;
    private Map<String, Integer> spawns; 
    private String default_spawn;
    private String kit;
    private String display_name;
    private String container_name;
    private Location loc;
    private LivingEntity uid;
    private EntityType type;
    private int id;

    public Selector(LivingEntity uid, String ds, String k, String dn, String cn)
    {
        this.uid = uid;
        this.spawns = new HashMap<>();
        this.default_spawn = ds;
        this.kit = k;
        this.display_name = dn;
        this.container_name = cn;
        this.id = ++id_count;
        this.type = uid.getType();
        this.loc = uid.getLocation();
    }

    public Selector(Location l, EntityType type, String default_spawn, String kit, String display_name, String container_name, int id, Map<String, Integer> spawns)
    {
        this.loc = l;
        this.type = type;
        this.default_spawn = default_spawn;
        this.kit = kit;
        this.display_name = display_name;
        this.container_name = container_name;
        this.id = id;
        this.spawns = spawns;
    }

    public Map<String, Integer> getSpawns()
    {
        return this.spawns;
    }

    public String getDefaultSpawn()
    {
        return this.default_spawn;
    }

    public String getKit()
    {
        return this.kit;
    }

    public String getDisplayName()
    {
        return this.display_name;
    }

    public String getContainerName()
    {
        return this.container_name;
    }

    public int getID()
    {
        return this.id;
    }

    public boolean addSpawn(String e)
    {
        if (spawns.size() >= 27) return false;
        spawns.put(e, spawns.size());
        return true;
    }

    public void removeSpawn(String e)
    {
        spawns.remove(e);
    }

    public void setDefaultSpawn(String s)
    {
        this.default_spawn = s;
    }

    public void setKit(String k)
    {
        this.kit = k;
    }

    public LivingEntity getEntity() 
    {
        return this.uid;
    }

    public Location getLocation() 
    {
        return this.loc;
    }

    public void setEntity(LivingEntity l) 
    {
        this.uid = l;
    }

    public EntityType getType() 
    {
        return this.type;
    }
}