package com.gmail.prizmahdiep;

import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.prizmahdiep.commands.CommandHandler;
import com.gmail.prizmahdiep.database.KitDatabase;
import com.gmail.prizmahdiep.database.SpawnDatabase;
import com.gmail.prizmahdiep.listeners.FFAPlayerLoadListener;
import com.gmail.prizmahdiep.listeners.FFAPlayerUnloadListener;
import com.gmail.prizmahdiep.listeners.PlayerDeathListener;
import com.gmail.prizmahdiep.listeners.PlayerDisconnectListener;
import com.gmail.prizmahdiep.listeners.PlayerInteractListener;
import com.gmail.prizmahdiep.listeners.PlayerJoinListener;
import com.gmail.prizmahdiep.listeners.PlayerRespawnListener;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;

import co.aikar.commands.PaperCommandManager;


public class FFAUtils extends JavaPlugin
{
    private FFAPlayersManager ffa_players_handler;
    private SpawnManager spawn_handler;
    private KitManager kit_handler;
    private CommandHandler command_handler;
    private KitDatabase kit_database;
    private SpawnDatabase spawn_database;
    
    @Override
    public void onEnable()
    {
        getLogger().info("Starting FFAUtils");
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        kit_database = new KitDatabase(getDataFolder().getAbsolutePath() + "/kits.db");
        kit_handler = new KitManager(this, kit_database);

        spawn_database = new SpawnDatabase(getDataFolder().getAbsolutePath() + "/spawns.db");
        spawn_handler = new SpawnManager(spawn_database);
        

        ffa_players_handler = new FFAPlayersManager(spawn_handler);

        command_handler = new CommandHandler(new PaperCommandManager(this));
        command_handler.registerCommands(spawn_handler, kit_handler, ffa_players_handler);
        registerFFAUtilsEvents();
    }
    
    @Override
    public void onDisable()
    {
        try 
        {
            kit_database.closeConnection();
            spawn_database.closeConnection();
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
        getLogger().info("Stopping FFAUtils");
    }

    private void registerFFAUtilsEvents()
    {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(spawn_handler, ffa_players_handler), this);
        getServer().getPluginManager().registerEvents(new FFAPlayerLoadListener(kit_handler, spawn_handler), this);
        getServer().getPluginManager().registerEvents(new FFAPlayerUnloadListener(/*ku,*/ spawn_handler), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(ffa_players_handler, spawn_handler), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(ffa_players_handler, this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(ffa_players_handler, kit_handler), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(spawn_handler, kit_handler, ffa_players_handler, this), this);
    }
}