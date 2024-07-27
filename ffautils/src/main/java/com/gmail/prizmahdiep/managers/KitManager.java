package com.gmail.prizmahdiep.managers;

import java.util.Map;
import java.util.Set;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.database.KitDatabase;
import com.gmail.prizmahdiep.objects.PlayerKit;

public class KitManager 
{
    public static Map<String, PlayerKit> kits;
    private FFAUtils plugin;
    private KitDatabase kit_database;

    public KitManager(FFAUtils pl, KitDatabase kdb) 
    {
        this.plugin = pl;
        this.kit_database = kdb;
        kits = new HashMap<>();
        reloadKits();
    }

    public boolean createKit(String name, ItemStack[] inv, Collection<PotionEffect> pf, boolean restorable)
    {
        PlayerKit k = new PlayerKit(name, inv, pf, restorable);
        try
        {
            if (!kit_database.kitExists(k.getName()) && !kits.containsKey(name))
            {
                kit_database.addKit(k);
                kits.put(name, k);
                return true;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeKit(String kitname)
    {
        try 
        {
            kit_database.removeKit(kitname);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setPlayerKit(String k_name, Player p)
    {
        if (!kits.containsKey(k_name))
        {
            plugin.getLogger().info("Kit " + k_name + " does not exists");
            return false;
        }
        PlayerKit k = kits.get(k_name);

        p.getInventory().setContents(k.getInventoryContents());
        p.clearActivePotionEffects();
        p.addPotionEffects(k.getPotionEffects());
        
        return true;
    }

    public int reloadKits()
    {
        kits.clear();
        try 
        {
            kits = kit_database.getKits();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return kits.size();
    }

    public Set<String> getExistingKitNames()
    {
        return kits.keySet();
    }
}
