package com.gmail.prizmahdiep;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.prizmahdiep.commands.CommandHandler;
import com.gmail.prizmahdiep.config.KitConfig;
import com.gmail.prizmahdiep.config.SpawnConfig;
import com.gmail.prizmahdiep.events.FFAPlayerLoadListener;
import com.gmail.prizmahdiep.events.FFAPlayerUnloadListener;
import com.gmail.prizmahdiep.events.PlayerDisconnectListener;
import com.gmail.prizmahdiep.events.PlayerJoinListener;
import com.gmail.prizmahdiep.events.PlayerRespawnListener;
import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.handlers.KitHandler;
import com.gmail.prizmahdiep.handlers.SpawnHandler;

import co.aikar.commands.PaperCommandManager;


public class FFAUtils extends JavaPlugin
{
    private SpawnConfig spawn_config;
    private KitConfig kit_config;
    private FFAPlayersHandler ffa_players_handler;
    private SpawnHandler spawn_handler;
    private KitHandler kit_handler;
    private CommandHandler command_handler;
    
    @Override
    public void onEnable()
    {
        getLogger().info("Starting FFAUtils");
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        
        spawn_config = new SpawnConfig(this);
        kit_config = new KitConfig(this);
        ffa_players_handler = new FFAPlayersHandler();
        spawn_handler = new SpawnHandler(this, spawn_config);
        kit_handler = new KitHandler(null, kit_config);
        command_handler = new CommandHandler(new PaperCommandManager(this));
    
        spawn_config.createSpawnConfiguration();
        kit_config.createKitsConfiguration();

        command_handler.registerCommands(spawn_handler, kit_handler, ffa_players_handler);
        registerFFAUtilsEvents();
    }
    
    @Override
    public void onDisable()
    {
        getLogger().info("Stopping FFAUtils");
    }

    private void registerFFAUtilsEvents()
    {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(spawn_handler, ffa_players_handler), this);
        getServer().getPluginManager().registerEvents(new FFAPlayerLoadListener(kit_handler, spawn_handler), this);
        getServer().getPluginManager().registerEvents(new FFAPlayerUnloadListener(/*ku,*/ spawn_handler), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(ffa_players_handler, spawn_handler, this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(ffa_players_handler, spawn_handler, this), this);
    }
}