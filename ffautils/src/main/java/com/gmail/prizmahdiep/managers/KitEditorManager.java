package com.gmail.prizmahdiep.managers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.gmail.prizmahdiep.database.EditedKitsDatabase;
import com.gmail.prizmahdiep.events.KitEditorPlayerLoadEvent;
import com.gmail.prizmahdiep.events.KitEditorPlayerUnloadEvent;
import com.gmail.prizmahdiep.objects.Kit;

public class KitEditorManager 
{
    private Map<UUID, Kit> kit_editor_players;
    private EditedKitsDatabase ekdb;

    public KitEditorManager(EditedKitsDatabase ekdb)
    {
        this.ekdb = ekdb;
        kit_editor_players = new HashMap<>();
    }

    public void load_player(UUID p, Kit kit_to_edit) 
    {
        kit_editor_players.put(p, kit_to_edit);
        Bukkit.getServer().getPluginManager().callEvent(new KitEditorPlayerLoadEvent(Bukkit.getPlayer(p), kit_to_edit));
    }

    public boolean EditedKitExists(UUID p, String name)
    {
        try 
        {
            return ekdb.editedKitExists(p, name);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return false;
    }

    public void unload_player(UUID p) 
    {
        kit_editor_players.remove(p);
        Bukkit.getServer().getPluginManager().callEvent(new KitEditorPlayerUnloadEvent(Bukkit.getPlayer(p)));
    }

    public boolean saveEditedKit(UUID p, ItemStack[] contents, String name)
    {
        try 
        {
            if (!EditedKitExists(p, name))
            {
                ekdb.addEditedKit(p, name, contents);
                return true;
            }
            else
            {
                ekdb.modifyEditedKit(p, name, contents);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return false;
    }

    public ItemStack[] getEditedKit(UUID p, String name)
    {
        try 
        {
            return ekdb.getEditedKit(p, name);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return null;
    }

    public Map<UUID, Kit> getPlayers() 
    {
        return this.kit_editor_players;
    }

    public void removeEditableKit(String name) 
    {
        try
        {
            ekdb.removeAllEditedKits(name);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
