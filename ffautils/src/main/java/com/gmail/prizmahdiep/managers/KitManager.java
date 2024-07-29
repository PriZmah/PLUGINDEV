package com.gmail.prizmahdiep.managers;

import java.util.Map;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.database.KitDatabase;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.KitInterface;

public class KitManager
{
    public static Map<String, KitInterface> kits;
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
        Kit k = new Kit(name, inv, pf, restorable);
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
        KitInterface k = kits.get(k_name);
        
        p.getInventory().setContents(k.getInventory());
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

    public void restorePlayerKit(FFAPlayer fp) 
    {
        fp.getPlayer().getInventory().setContents(fp.getPlayerKit().getInventory());
        fp.getPlayer().clearActivePotionEffects();
        fp.getPlayer().addPotionEffects(fp.getPlayerKit().getPotionEffects());
    }
}
