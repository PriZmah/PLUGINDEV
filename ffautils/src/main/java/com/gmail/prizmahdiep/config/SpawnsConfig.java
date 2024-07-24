package com.gmail.prizmahdiep.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class SpawnsConfig 
{
    private final String config_filename = "spawns.yml";
    private FFAUtils plugin;
    private File spawns_configuration_file;
    private FileConfiguration spawns_configuration;

    public SpawnsConfig(FFAUtils pl)
    {
        this.plugin = pl;
    }

    public FileConfiguration getSpawnsConfiguration()
    {
        return this.spawns_configuration;
    }

    public void save()
    {
        try 
        {
            getSpawnsConfiguration().save(spawns_configuration_file);    
        } 
        catch (IOException e) 
        {
            plugin.getLogger().warning("Unable to save " + config_filename);
        }
    }

    public void createSpawnConfiguration()
    {
        spawns_configuration_file = new File(plugin.getDataFolder(), config_filename);
        if (!spawns_configuration_file.exists())
        {
            try 
            {
                spawns_configuration_file.createNewFile();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        spawns_configuration = new YamlConfiguration();
        try 
        {
            spawns_configuration.load(spawns_configuration_file);
        } 
        catch (IOException | InvalidConfigurationException e) 
        {
            e.printStackTrace();
        }
    }

    public boolean createSpawnConfigEntry(SpawnLocation spawn)
    {
        if (!spawns_configuration.contains(spawn.getName()))
        {
            spawns_configuration.set(spawn.getName() + ".x", spawn.getLocation().getX());
            spawns_configuration.set(spawn.getName() + ".y", spawn.getLocation().getY());
            spawns_configuration.set(spawn.getName() + ".z", spawn.getLocation().getZ());
            spawns_configuration.set(spawn.getName() + ".pitch", spawn.getLocation().getPitch());
            spawns_configuration.set(spawn.getName() + ".yaw", spawn.getLocation().getYaw());
            spawns_configuration.set(spawn.getName() + ".world", spawn.getLocation().getWorld().getName());
            spawns_configuration.set(spawn.getName() + ".type", spawn.getType());
            save();
            plugin.getLogger().info("Creating spawn named: " + spawn.getName());
        }
        else
        {
            plugin.getLogger().info("Spawn " + spawn.getName() + " already exists");
            return false;
        } 
        return true;
    }

    public boolean removeSpawnConfigEntry(String name)
    {
        if (spawns_configuration.contains(name))
        {
            spawns_configuration.set(name, null);
            save();
            plugin.getLogger().info("Deleting spawn: " + name);
        }
        else
        {
            plugin.getLogger().info("Spawn " + name + " does not exists");
            return false;
        } 
        return true;
    }

    public Map<String, SpawnLocation> getSpawnObjects() 
    {
        try 
        {
            spawns_configuration.load(spawns_configuration_file);
        } 
        catch (IOException | InvalidConfigurationException e) 
        {
            e.printStackTrace();
        }
        Map<String, SpawnLocation> ret = new HashMap<>();
        
        double x, y, z, pitch, yaw;
        String world_name, type;

        for (String k : spawns_configuration.getKeys(false))
        {
            x = spawns_configuration.getDouble(k + ".x");
            y = spawns_configuration.getDouble(k + ".y");
            z = spawns_configuration.getDouble(k + ".z");
            pitch = spawns_configuration.getDouble(k + ".pitch");
            yaw = spawns_configuration.getDouble(k + ".yaw");
            world_name = spawns_configuration.getString(k + ".world");
            type = spawns_configuration.getString(k + ".type");

            ret.put(k, new SpawnLocation(k, new Location(Bukkit.getWorld(world_name), x, y, z, (float) yaw, (float) pitch), type));
        }

       return ret;
    }
}
