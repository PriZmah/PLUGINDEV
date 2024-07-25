package com.gmail.prizmahdiep;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.prizmahdiep.utils.KitUtils;
import com.gmail.prizmahdiep.utils.SpawnUtils;

import com.gmail.prizmahdiep.commands.CommandHandler;
import com.gmail.prizmahdiep.config.KitsConfig;
import com.gmail.prizmahdiep.config.SpawnsConfig;
import com.gmail.prizmahdiep.events.FFAPlayerLoadListener;
import com.gmail.prizmahdiep.events.FFAPlayerUnloadListener;
import com.gmail.prizmahdiep.events.PlayerDisconnectListener;
import com.gmail.prizmahdiep.events.PlayerJoinListener;
import com.gmail.prizmahdiep.events.PlayerRespawnListener;
import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;

import co.aikar.commands.PaperCommandManager;

public class FFAUtils extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        getLogger().info("Starting FFAUtils");
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        
        SpawnsConfig scfg = new SpawnsConfig(this);
        scfg.createSpawnConfiguration();
        
        KitsConfig kcfg = new KitsConfig(this);
        kcfg.createKitsConfiguration();

        FFAPlayersHandler fph = new FFAPlayersHandler();
        KitUtils ku = new KitUtils(this, kcfg);
        SpawnUtils su = new SpawnUtils(this, scfg);
        CommandHandler handler = new CommandHandler(new PaperCommandManager(this));

        handler.registerCommands(su, ku, fph, su);
        registerFFAUtilsEvents(ku, su, fph);
    }
    
    @Override
    public void onDisable()
    {
        getLogger().info("Stopping FFAUtils");
    }

    private void registerFFAUtilsEvents(KitUtils ku, SpawnUtils su, FFAPlayersHandler fph)
    {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(su, fph), this);
        getServer().getPluginManager().registerEvents(new FFAPlayerLoadListener(ku, su), this);
        getServer().getPluginManager().registerEvents(new FFAPlayerUnloadListener(/*ku,*/ su), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(fph, su, this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(fph, su, this), this);
    }
}