package com.gmail.prizmahdiep.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.SpawnUtils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("ffautils.unloadme")
@CommandAlias("unloadme|unldme")
public class CommandUnloadme extends BaseCommand
{
    private FFAPlayersHandler fph;
    private SpawnUtils sputils;

    public CommandUnloadme(FFAPlayersHandler fph, SpawnUtils sputils)
    {
        this.fph = fph;
        this.sputils = sputils;
    }

    @Default
    public void onUnloadme(Player p)
    {
        FFAPlayer ffap = FFAPlayersHandler.ffa_players.get(p.getUniqueId());
        SpawnLocation main_spawn = sputils.getMainSpawn();
        
        if (ffap == null)
        {
            p.sendMessage(ChatColor.RED + "You are not in FFA");
            Bukkit.getServer().getLogger().warning("Tried to unload an unloaded player");
            return;
        }
        
        ffap.setPlayerKit(null);
        ffap.setPlayerKit(null);
        
        if (main_spawn != null)
        {
            ffap.setPlayerSpawn(main_spawn);
            ffap.setPlayerSpawn(main_spawn);
        }
        else p.sendMessage(ChatColor.RED + "Main spawn does not exist");


        fph.removePlayerFromFFA(p);
    }
}
