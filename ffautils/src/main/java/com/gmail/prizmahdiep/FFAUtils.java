package com.gmail.prizmahdiep;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.prizmahdiep.commands.CommandHandler;
import com.gmail.prizmahdiep.config.BlockedCommandsConfig;
import com.gmail.prizmahdiep.config.DeathMessagesConfig;
import com.gmail.prizmahdiep.database.EditedKitsDatabase;
import com.gmail.prizmahdiep.listeners.FFAListener;
import com.gmail.prizmahdiep.listeners.KitEditorListener;
import com.gmail.prizmahdiep.listeners.PlayerJoinQuitListener;
import com.gmail.prizmahdiep.listeners.SelectorListener;
import com.gmail.prizmahdiep.managers.BlockedCommandsManager;
import com.gmail.prizmahdiep.managers.CombatTagManager;
import com.gmail.prizmahdiep.managers.DeathMessagesManager;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.RespawnItemManager;
import com.gmail.prizmahdiep.managers.SelectorManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.Selector;
import com.gmail.prizmahdiep.utils.PlayerUtils;

import co.aikar.commands.PaperCommandManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class FFAUtils extends JavaPlugin
{
    private DeathMessagesConfig death_messages_config;
    private BlockedCommandsConfig blocked_commands_config;
    private KitManager kit_manager;
    private SpawnManager spawn_manager;
    private FFAPlayersManager ffa_players_manager;
    private CommandHandler command_handler;
    private EditedKitsDatabase kit_editor_database;
    private KitEditorManager kit_editor_manager;
    private SelectorManager selector_manager;
    private DeathMessagesManager death_messages_manager;
    private BlockedCommandsManager blocked_commands_manager;
    private CombatTagManager combat_tag_manager;
    private MiniMessage minimessage_serializer;
    private RespawnItemManager respawn_item_manager;
    
    @Override
    public void onEnable()
    {
        getLogger().info("Starting FFAUtils");
        minimessage_serializer = MiniMessage.miniMessage();
        File data_folder = getDataFolder();
        File kits_folder = new File(data_folder, "kits");
        File spawns_folder = new File(data_folder, "spawns");
        File selectors_folder = new File(data_folder, "selectors");

        if (!data_folder.exists()) data_folder.mkdir();
        createSubFolders(data_folder, kits_folder, spawns_folder, selectors_folder);
        createConfigs();
        
        try 
        {
            kit_manager = new KitManager(kits_folder);
            spawn_manager = new SpawnManager(spawns_folder);
            selector_manager = new SelectorManager(selectors_folder, minimessage_serializer, kit_manager, this);
            kit_editor_database = new EditedKitsDatabase(getDataFolder().getAbsolutePath() + "/edited_kits.db");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        
        death_messages_manager = new DeathMessagesManager(death_messages_config, minimessage_serializer);
        blocked_commands_manager = new BlockedCommandsManager(blocked_commands_config);
        ffa_players_manager = new FFAPlayersManager();
        kit_editor_manager = new KitEditorManager(kit_editor_database, ffa_players_manager);

        combat_tag_manager = new CombatTagManager(this, ffa_players_manager);

        respawn_item_manager = new RespawnItemManager(minimessage_serializer, this);
        
        command_handler = new CommandHandler(new PaperCommandManager(this), this);
        command_handler.registerCommands(spawn_manager, kit_manager, ffa_players_manager, kit_editor_manager, selector_manager, minimessage_serializer, respawn_item_manager, death_messages_manager, blocked_commands_manager);
        new PlayerUtils(ffa_players_manager, spawn_manager, kit_manager, this, kit_editor_manager);


        registerFFAUtilsEvents();
    }

    private void createConfigs() 
    {
        saveDefaultConfig();
        death_messages_config = new DeathMessagesConfig(this);
        blocked_commands_config = new BlockedCommandsConfig(this);
    }

    private void createSubFolders(File... folders) 
    {
        for (File folder : folders)
            if (!folder.exists()) folder.mkdir();
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Stopping FFAUtils");
        try 
        {
            if (kit_editor_database != null)
                kit_editor_database.closeConnection();
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }

        for (Selector i : selector_manager.getSelectors().values()) i.getEntity().remove();
    }

    private void registerFFAUtilsEvents()
    {
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(spawn_manager, ffa_players_manager, combat_tag_manager, kit_editor_manager), this);
        getServer().getPluginManager().registerEvents(new FFAListener(spawn_manager, kit_manager, ffa_players_manager, this, kit_editor_manager, combat_tag_manager, respawn_item_manager, death_messages_manager, minimessage_serializer, blocked_commands_manager), this);
        //getServer().getPluginManager().registerEvents(new TestListener(), this);
        getServer().getPluginManager().registerEvents(new KitEditorListener(spawn_manager, ffa_players_manager, this, kit_editor_manager), this);
        getServer().getPluginManager().registerEvents(new SelectorListener(this, selector_manager, kit_manager, spawn_manager, ffa_players_manager, minimessage_serializer), this);
    }
}