package com.gmail.prizmahdiep;

import org.bukkit.plugin.java.JavaPlugin;


public class GoldenHeadDetector3000 extends JavaPlugin
{
    public static GoldenHeadDetector3000 PLUGINSITO;

    @Override
    public void onEnable()
    {
        this.getServer().getPluginManager().registerEvents(new OnPlayerRespawnEvent(), this);
    }

    @Override
    public void onDisable()
    {

    }
}