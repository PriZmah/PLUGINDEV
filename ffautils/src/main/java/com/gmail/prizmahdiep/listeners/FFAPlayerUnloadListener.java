package com.gmail.prizmahdiep.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.events.FFAPlayerUnloadEvent;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.utils.PlayerUtils;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class FFAPlayerUnloadListener implements Listener
{
    private SpawnManager su;
    private FFAPlayersManager fph;
    private FFAUtils futils;

    public FFAPlayerUnloadListener(SpawnManager su, FFAPlayersManager fph, FFAUtils futils)
    {
        this.su = su;
        this.fph = fph;
        this.futils = futils;
    }

    @EventHandler
    public void onPlayerUnload(FFAPlayerUnloadEvent p)
    {
        FFAPlayer pf = p.getFFAPlayer();
        Player pi = pf.getPlayer();
        
        su.teleportEntityToSpawn(pf.getChosenSpawn().getName(), pi);
        pi.getInventory().setContents(pf.getPlayerKit().getInventory());
        pi.clearActivePotionEffects();
        pi.addPotionEffects(pf.getPlayerKit().getPotionEffects());
        PlayerUtils.resetPlayerStatus(pi);

        if (!fph.isIdle(pf.getUUID())) return;

        ItemStack respawn_item = new ItemStack(Material.PINK_DYE);
        NamespacedKey key = new NamespacedKey(futils, "respawn-item-type");
        ItemMeta respawn_item_meta = respawn_item.getItemMeta();
        respawn_item_meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        respawn_item_meta.displayName(Component.text(ChatColor.LIGHT_PURPLE + "Respawn to last location"));
        respawn_item.setItemMeta(respawn_item_meta);

        pf.getPlayer().getInventory().setItemInMainHand(respawn_item);
    }
}
