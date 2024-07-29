package com.gmail.prizmahdiep.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class PlayerRespawnListener implements Listener
{
    private FFAPlayersManager fph;
    private FFAUtils futils;

    public PlayerRespawnListener(FFAPlayersManager fph, FFAUtils pl) 
    {
        this.fph = fph;
        this.futils = pl;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent ev)
    {   
        Player p = ev.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() 
            {
                resetPlayer(p);
            }
        }.runTaskLater(futils, 1);
    }

    private void resetPlayer(Player p) 
    {
        fph.movePlayerFromFFA(p);
        if (!fph.isIdle(p.getUniqueId())) return;

        ItemStack respawn_item = new ItemStack(Material.PINK_DYE);
        NamespacedKey key = new NamespacedKey(futils, "respawn-item-type");
        ItemMeta respawn_item_meta = respawn_item.getItemMeta();
        respawn_item_meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        respawn_item_meta.displayName(Component.text(ChatColor.LIGHT_PURPLE + "Respawn to last location"));
        respawn_item.setItemMeta(respawn_item_meta);

        FFAPlayer pf = FFAPlayersManager.idle_ffa_players.get(p.getUniqueId());
        pf.getPlayer().getInventory().setItemInMainHand(respawn_item);
    }
}