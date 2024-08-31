package com.gmail.prizmahdiep;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.prizmahdiep.commands.CommandHandler;
import com.gmail.prizmahdiep.database.EditedKitsDatabase;
import com.gmail.prizmahdiep.listeners.FFAListener;
import com.gmail.prizmahdiep.listeners.KitEditorListener;
import com.gmail.prizmahdiep.listeners.PlayerJoinQuitListener;
//import com.gmail.prizmahdiep.listeners.TestListener;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.utils.PlayerUtils;

import co.aikar.commands.PaperCommandManager;


public class FFAUtils extends JavaPlugin
{
    private KitManager kit_manager;
    private SpawnManager spawn_manager;
    private FFAPlayersManager ffa_players_handler;
    private CommandHandler command_handler;
    private EditedKitsDatabase kit_editor_database;
    private KitEditorManager kit_editor_manager;
    
    @Override
    public void onEnable()
    {
        getLogger().info("Starting FFAUtils");
        File data_folder = getDataFolder();
        File config_folder = new File(data_folder, "config");
        File kits_folder = new File(data_folder, "kits");
        File spawns_folder = new File(data_folder, "spawns");

        if (!data_folder.exists()) data_folder.mkdir();
        createSubFolders(data_folder, config_folder, kits_folder, spawns_folder);

        try 
        {
            kit_manager = new KitManager(kits_folder);
            spawn_manager = new SpawnManager(spawns_folder);
            kit_editor_database = new EditedKitsDatabase(getDataFolder().getAbsolutePath() + "/edited_kits.db");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        
        kit_editor_manager = new KitEditorManager(kit_editor_database);
        ffa_players_handler = new FFAPlayersManager();

        command_handler = new CommandHandler(new PaperCommandManager(this), this);
        command_handler.registerCommands(spawn_manager, kit_manager, ffa_players_handler, kit_editor_manager);
        new PlayerUtils(ffa_players_handler, spawn_manager, kit_manager);
        registerFFAUtilsEvents();
    }
    
    private void createSubFolders(File... folders) 
    {
        for (File folder : folders)
            if (!folder.exists()) folder.mkdir();
    }

    @Override
    public void onDisable()
    {
        try 
        {
            if (kit_editor_database != null)
                kit_editor_database.closeConnection();
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
        getLogger().info("Stopping FFAUtils");
    }

    private void registerFFAUtilsEvents()
    {
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(spawn_manager, ffa_players_handler), this);
        getServer().getPluginManager().registerEvents(new FFAListener(spawn_manager, kit_manager, ffa_players_handler, this, kit_editor_manager), this);
        //getServer().getPluginManager().registerEvents(new TestListener(), this);
        getServer().getPluginManager().registerEvents(new KitEditorListener(spawn_manager, kit_manager), this);
    }
}