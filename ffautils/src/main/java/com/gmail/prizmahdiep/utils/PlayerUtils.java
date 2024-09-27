package com.gmail.prizmahdiep.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.SpawnLocation;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class PlayerUtils 
{
    private static FFAPlayersManager fph;
    private static SpawnManager sm;
    private static KitManager km;
    private static FFAUtils futils;
    private static KitEditorManager kem;
    public PlayerUtils (FFAPlayersManager fpa, SpawnManager sma, KitManager kma, FFAUtils fu, KitEditorManager kemm)
    {
        fph = fpa;
        sm = sma;
        km = kma;
        futils = fu;
        kem = kemm;
    }

    public static void resetPlayerStatus(Player p)
    {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(0);
    }

    public static void resetPlayer(Player p)
    {
        SpawnLocation a = sm.getSpawnOfType(SpawnLocation.SPAWN);
        if (sm.isValidSpawn(a))
            p.teleport(a.getLocation());
        p.getInventory().clear();
        p.clearActivePotionEffects();
        resetPlayerStatus(p);
    }

    public static void resetFFAPlayer(FFAPlayer p)
    {
        resetPlayerStatus(p.getPlayer());
        fph.movePlayerFromFFA(p);
    }

    public static void clearFFAPlayer(FFAPlayer p)
    {
        resetFFAPlayer(p);
        fph.removePlayerFromIdle(p.getUUID());
    }

    public static void setPlayerKit(Player p, Kit k)
    {
        p.clearActivePotionEffects();
        p.addPotionEffects(k.getPotionEffects());
        p.getInventory().setContents(k.getInventory());
    }

    public static void restorePlayerKit(FFAPlayer fp) 
    {
        Player p = fp.getPlayer();
        Kit k = km.getKits().get(fp.getCurrentPlayerKitName());
        if (k != null)
        {
            setPlayerKit(p, k);
            if (k.isEditable())
            {
                new BukkitRunnable() 
                {
                    @Override
                    public void run()
                    {
                        if (kem.EditedKitExists(p.getUniqueId(), k.getName()))
                        {
                            ItemStack[] overr = kem.getEditedKit(p.getUniqueId(), k.getName());
                            new BukkitRunnable() 
                            {
                                @Override
                                public void run()
                                {
                                    p.getInventory().setContents(overr);
                                }
                            }.runTask(futils);
                        }
                    }    
                }.runTaskAsynchronously(futils);
            }
        }
        else fp.getPlayer().sendMessage(ChatColor.RED + "This kit does not exist");
    }

    public static boolean teleportPlayerToSpawn(Player p, SpawnLocation s)
    {
        boolean flag = false;
            if (flag = sm.isValidSpawn(s)) p.teleport(s.getLocation());
        return flag;
    }

    public static ItemStack getRespawnItem()
    {
        ItemStack respawn_item = new ItemStack(Material.getMaterial("PINK_DYE"));

        NamespacedKey key = new NamespacedKey(futils, "respawn-item-type");

        ItemMeta respawn_item_meta = respawn_item.getItemMeta();
        
        respawn_item_meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        respawn_item_meta.displayName(Component.text(ChatColor.LIGHT_PURPLE + "Respawn to last location"));

        respawn_item.setItemMeta(respawn_item_meta);

        return respawn_item;
    }
}
