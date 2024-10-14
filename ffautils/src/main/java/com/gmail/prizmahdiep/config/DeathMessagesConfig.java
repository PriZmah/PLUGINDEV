package com.gmail.prizmahdiep.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.prizmahdiep.FFAUtils;

public class DeathMessagesConfig 
{
    private File config_file;
    private String config_filename = "death-messages.yml";
    private FileConfiguration config;
    private FFAUtils futils;

    public DeathMessagesConfig(FFAUtils fu)
    {
        this.futils = fu;
        futils.saveResource(config_filename, false);
        reload();
    }

    public FileConfiguration getConfig()
    {
        return this.config;
    }

    public void reload()
    {
        config_file = new File(futils.getDataFolder(), config_filename);
        config = new YamlConfiguration();
        try 
        {
            config.load(config_file);
        } 
        catch (IOException | InvalidConfigurationException e) 
        {
            e.printStackTrace();
        }
    }
}
