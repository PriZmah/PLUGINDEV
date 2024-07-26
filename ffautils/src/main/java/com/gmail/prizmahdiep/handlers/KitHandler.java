package com.gmail.prizmahdiep.handlers;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.config.KitConfig;
import com.gmail.prizmahdiep.objects.PlayerKit;

public class KitHandler 
{
    public static Map<String, PlayerKit> kits;
    private FFAUtils plugin;
    private KitConfig kitsconf;

    public KitHandler(FFAUtils pl, KitConfig kcfg) 
    {
        this.plugin = pl;
        this.kitsconf = kcfg;
        kits = new HashMap<>();
        reloadKits();
    }

    public boolean createKit(String name, ItemStack[] inv, Collection<PotionEffect> pf)
    {
        if (kits.containsKey(name)) return false;

        PlayerKit kit = new PlayerKit(name, inv, pf);
        if (!kitsconf.createKitConfigEntry(kit)) return false;
        kits.put(name, kit);
        return true;
    }

    public boolean removeKit(String kitname)
    {
        if (!kitsconf.removeKitConfigEntry(kitname)) return false;
        kits.remove(kitname);
        return true;
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
        p.addPotionEffects(k.getEffects());
        
        return true;
    }

    public int reloadKits()
    {
        int loaded_kits = 0;
        kits.clear();
        kits.putAll(kitsconf.getKitObjects());
        loaded_kits = kits.size();
        plugin.getLogger().info(loaded_kits + " kits loaded");
        return loaded_kits;
    }

    public Set<String> getExistingKits()
    {
        return kits.keySet();
    }
}
