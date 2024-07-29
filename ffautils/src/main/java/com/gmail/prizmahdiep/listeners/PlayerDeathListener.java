package com.gmail.prizmahdiep.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class PlayerDeathListener implements Listener
{
    private FFAPlayersManager fph;
    private KitManager km;

    public PlayerDeathListener(FFAPlayersManager fph, KitManager km) 
    {
        this.fph = fph;
        this.km = km;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev)
    {
        if (ev.getPlayer().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK))
        {
            /*new BukkitRunnable() {
                @Override
                public void run()
                {
                }
                
            }.runTaskLater(futils, 1);*/
            clean(ev);
        }
    }
    
    private void clean(PlayerDeathEvent ev) 
    {   
        Player victim = ev.getEntity();
        Player killer = ev.getEntity().getKiller();
  
        double max_health = 0;
        double current_health = 0;
        double needed_health = 0;

        if (fph.isOnFFA(victim.getUniqueId()))
            ev.getDrops().clear();
        
        
            
            if (fph.isOnFFA((killer.getUniqueId())))
            {
                max_health = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                current_health = killer.getHealth();
                needed_health = max_health - current_health;
                killer.setHealth(current_health + needed_health);
                killer.sendActionBar(Component.text(ChatColor.GREEN + "Healed " + (double) Math.round(needed_health * 100) / 100 + " HP"));
                
                if (fph.isOnFFA(victim.getUniqueId()))
                    ev.deathMessage(Component.text(ChatColor.RED + victim.getName() + ChatColor.YELLOW + " was slain by " 
                    + ChatColor.GREEN + killer.getName() + ChatColor.YELLOW + " with " + String.valueOf(current_health) + " HP"));
                
            FFAPlayer fp = FFAPlayersManager.ffa_players.get(killer.getUniqueId());
            if (fp.getPlayerKit().isRestorable())
                km.restorePlayerKit(fp);
        }
    }
}
