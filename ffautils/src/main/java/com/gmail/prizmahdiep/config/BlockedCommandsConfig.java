package com.gmail.prizmahdiep.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.prizmahdiep.FFAUtils;

public class BlockedCommandsConfig 
{
    private final String filename = "blocked-commands.yml";
    private File file;
    private FileConfiguration config;
    private FFAUtils futils;

    public BlockedCommandsConfig(FFAUtils fu)
    {
        this.futils = fu;
        futils.saveResource(filename, false);
        reload();
    }

    public FileConfiguration getConfig()
    {
        return this.config;
    }

    public void reload()
    {
        file = new File(futils.getDataFolder(), filename);
        config = new YamlConfiguration();
        try 
        {
            config.load(file);
        } 
        catch (IOException | InvalidConfigurationException e) 
        {
            e.printStackTrace();
        }
    }
}
