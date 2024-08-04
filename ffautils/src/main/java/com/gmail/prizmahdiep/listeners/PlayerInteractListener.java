package com.gmail.prizmahdiep.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;

public class PlayerInteractListener implements Listener
{
    SpawnManager sm;
    KitManager km;
    FFAPlayersManager fph;
    FFAUtils futils;

    public PlayerInteractListener(SpawnManager sm, KitManager km, FFAPlayersManager fph, FFAUtils futils)
    {
        this.sm = sm;
        this.km = km;
        this.fph = fph;
        this.futils = futils;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent ev)
    {
        ItemStack item = ev.getItem();
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return;
        
        Player p = ev.getPlayer();
        ItemMeta item_meta = ev.getItem().getItemMeta();

        if (item_meta == null) return;

        NamespacedKey key = new NamespacedKey(futils, "respawn-item-type");

        if (item_meta.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN) 
        && fph.isIdle(p.getUniqueId()))
        {
            FFAPlayer fp = FFAPlayersManager.idle_ffa_players.get(p.getUniqueId());
            fph.movePlayerFromIdle(fp);
        }
    }    
}
