package com.gmail.prizmahdiep.managers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.gmail.prizmahdiep.database.EditedKitsDatabase;
import com.gmail.prizmahdiep.events.KitEditorPlayerLoadEvent;
import com.gmail.prizmahdiep.events.KitEditorPlayerUnloadEvent;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.Kit;

public class KitEditorManager 
{
    private Map<UUID, Kit> kit_editor_players;
    private Map<UUID, FFAPlayer> idle_players;
    private EditedKitsDatabase ekdb;
    private FFAPlayersManager fph;

    public KitEditorManager(EditedKitsDatabase ekdb, FFAPlayersManager fph)
    {
        this.ekdb = ekdb;
        kit_editor_players = new HashMap<>();
        idle_players = new HashMap<>();
        this.fph = fph;
    }

    public void load_player(UUID p, Kit kit_to_edit) 
    {
        FFAPlayer a = fph.getFFAPlayers().get(p);
        if (a != null)
        {
            idle_players.put(p, a);
            fph.removePlayerFromIdle(p);
        }
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
        if (idle_players.containsKey(p))
        {
            fph.addPlayerToIdle(p, idle_players.get(p));
            idle_players.remove(p);
        }
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

    public Map<UUID, Kit> getKitEditorPlayers() 
    {
        return this.kit_editor_players;
    }

    public void removeEditableKits(String name) 
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

    public void removeEditableKits(UUID p) 
    {
        try
        {
            ekdb.removeAllEditedKits(p);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void removeEditableKits(UUID p, String kit_name) 
    {
        try
        {
            ekdb.removeAllEditedKits(p, kit_name);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public List<String> getEditedKitNames(UUID p)
    {
        List<String> names = new ArrayList<>();
        try 
        {
            names.addAll(ekdb.getPlayerEditedKitNames(p));
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return names;
    }

    public Map<UUID, FFAPlayer> getPreviouslyIdlePlayers()
    {
        return this.idle_players;
    }
}
