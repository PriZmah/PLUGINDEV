package com.gmail.prizmahdiep.objects;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class Selector
{
    private Map<String, Integer> spawns; 
    private String default_spawn;
    private String kit;
    private String display_name;
    private String container_name;
    private Location loc;
    private LivingEntity uid;
    private EntityType type;
    private String id;

    public Selector(LivingEntity uid, String ds, String k, String dn, String cn, String id)
    {
        this.uid = uid;
        this.spawns = new HashMap<>();
        this.default_spawn = ds;
        this.kit = k;
        this.display_name = dn;
        this.container_name = cn;
        this.id = id;
        this.type = uid.getType();
        this.loc = uid.getLocation();
    }

    public Selector(Location l, EntityType type, String default_spawn, String kit, String display_name, String container_name, String id, Map<String, Integer> spawns)
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

    public Selector(Selector s)
    {
        LivingEntity liv = s.getEntity();
        Location l = s.getLocation();
        EntityType t = s.getType();
        String dfs = s.getDefaultSpawn();
        String k = s.getKit();
        String ds = s.getDisplayName();
        String cn = s.getContainerName();
        String id = s.getID();
        Map<String, Integer> ss = s.getSpawns();

        this.uid = liv;
        this.loc = l;
        this.type = t;
        this.default_spawn = dfs;
        this.kit = k;
        this.display_name = ds;
        this.container_name = cn;
        this.id = id;
        this.spawns = ss;
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

    public String getID()
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

    public void setType(EntityType type)
    {
        this.type = type;
    }

    public void setDisplayName(String mm)
    {
        this.display_name = mm;
    }

    public void setContainerTitle(String minimsg) 
    {
        this.container_name = minimsg;
    }

    public void setLocation(Location location) 
    {
        this.loc = location;
    }

    public void setID(String newid) 
    {
        this.id = newid;
    }
}