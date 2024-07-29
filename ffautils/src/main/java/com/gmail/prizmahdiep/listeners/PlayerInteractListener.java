package com.gmail.prizmahdiep.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

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
        Player p = ev.getPlayer();
        if (p.getInventory().getItemInMainHand()
        .getItemMeta()
        .getPersistentDataContainer()
        .has(NamespacedKey.fromString("respawn-item-type")) && fph.isIdle(p.getUniqueId()))
        {
            FFAPlayer fp = FFAPlayersManager.ffa_players.get(p.getUniqueId());
            fph.addPlayerToFFA(p, fp.getPlayerKit(), fp.getChosenSpawn());
        }
    }    
}
