package com.gmail.prizmahdiep.managers;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;

import net.md_5.bungee.api.ChatColor;

public class CombatTagManager 
{
    public static long DEFAULT_COOLDOWN = 15;
    private final Map<UUID, Instant> tagged_players = new HashMap<>();
    //private FFAUtils pl;
    //private FFAPlayersManager fpm;

    public CombatTagManager(FFAUtils pl, FFAPlayersManager fpm) 
    {
        //this.pl = pl;

        new BukkitRunnable() 
        {
            @Override
            public void run()
            {
                Iterator<UUID> iterator = tagged_players.keySet().iterator();
                while (iterator.hasNext())
                {
                    UUID i = iterator.next();
                    if (!isTagged(i))
                    {
                        Bukkit.getPlayer(i).sendMessage(ChatColor.GREEN + "You are no longer in combat");
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimer(pl, 0L, 20L);
    }

    public void  setTag(UUID puid, Duration time)
    {
        tagged_players.put(puid, Instant.now().plus(time));
    }

    public boolean isTagged(UUID p)
    {
        Instant t = tagged_players.get(p);
        return t != null && Instant.now().isBefore(t);
    }

    public Instant removeTag(UUID p)
    {
        return tagged_players.remove(p);
    }

    public Duration getRemainingTime(UUID p)
    {
        Instant cooldown = tagged_players.get(p);
        Instant now = Instant.now();
        return cooldown != null && now.isBefore(cooldown) ? Duration.between(now, cooldown) : Duration.ZERO;
    }
}
