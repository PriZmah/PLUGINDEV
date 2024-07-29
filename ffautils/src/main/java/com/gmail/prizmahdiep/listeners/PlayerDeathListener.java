package com.gmail.prizmahdiep.listeners;

import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class PlayerDeathListener implements Listener
{
    private FFAUtils futils;
    private FFAPlayersManager fph;
    private KitManager km;

    public PlayerDeathListener(FFAPlayersManager fph, KitManager km, FFAUtils futils) 
    {
        this.fph = fph;
        this.futils = futils;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev)
    {
        if (ev.getPlayer().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK))
        {
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    clean(ev.getPlayer(), ev.getPlayer().getKiller(), ev.getDrops(), ev.deathMessage());
                }
    
            }.runTaskLater(futils, 1);
        }
    }

    private void clean(Player victim, Entity killer, List<ItemStack> drops, Component death_message) 
    {   
        double max_health = 0;
        double current_health = 0;
        double needed_health = 0;
        if (fph.isOnFFA(victim.getUniqueId()))
        {
            drops.clear();
            fph.movePlayerFromFFA(victim);
        }
    
        if (killer instanceof Player)
        {
            Player player_killer = (Player) killer;
            if (fph.isOnFFA((player_killer.getUniqueId())))
            {
                max_health = player_killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                current_health = player_killer.getHealth();
                needed_health = max_health - current_health;
                player_killer.setHealth(current_health + needed_health);
                player_killer.sendActionBar(Component.text(ChatColor.GREEN + "Healed " + needed_health + " HP"));
    
                FFAPlayer fp = FFAPlayersManager.ffa_players.get(player_killer.getUniqueId());
                if (fp.getPlayerKit().isRestorable())
                    km.restorePlayerKit(fp);
            }
        }

        if (fph.isOnFFA(victim.getUniqueId()) && fph.isOnFFA(killer.getUniqueId()))
        {
            final double current_health_final = current_health;
            death_message.replaceText((e) ->        
                e.replacement(ChatColor.RED + victim.getName() + ChatColor.YELLOW + " was slain by " 
                + ChatColor.GREEN + killer.getName() + ChatColor.YELLOW + " with " + String.valueOf(current_health_final) + " HP")
            );
        }
    }
}
