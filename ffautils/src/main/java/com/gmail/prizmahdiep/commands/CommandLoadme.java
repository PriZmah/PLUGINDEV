package com.gmail.prizmahdiep.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.PlayerKit;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.KitUtils;
import com.gmail.prizmahdiep.utils.SpawnUtils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("ffautils.loadme")
@CommandAlias("loadme|ldme")
public class CommandLoadme extends BaseCommand
{
    private FFAPlayersHandler fph;

    public CommandLoadme(FFAPlayersHandler fph)
    {
        this.fph = fph;
    }

    @Default
    @CommandCompletion("kit_name spawn_name")
    public void onLoadMe(Player p, String kit, String spawn)
    {
        SpawnLocation sa = SpawnUtils.spawns.get(spawn.toUpperCase());
        PlayerKit ka = KitUtils.kits.get(kit.toUpperCase());
        FFAPlayer ffap = FFAPlayersHandler.ffa_players.get(p.getUniqueId());

        if (ffap != null)
        {
            Bukkit.getServer().getLogger().warning("Tried to load a loaded player");
            p.sendMessage(ChatColor.RED + "You are already in FFA");
            return;
        }

        if (sa == null)
        {
            p.sendMessage(ChatColor.RED + "This spawn does not exist");
            return; 
        }

        if (ka == null)
        {
            p.sendMessage(ChatColor.RED + "This kit does not exist");
            return; 
        }

        if (!p.hasPermission("ffautils.loadme.kit." + kit))
        {
            p.sendMessage(ChatColor.RED + "You don't have permission to use this kit");
            return;  
        }

        if (!p.hasPermission("ffautils.loadme.spawn." + spawn))
        {
            p.sendMessage(ChatColor.RED + "You don't have permission to use this spawn");
            return;  
        }
        if (!sa.getType().equals(SpawnLocation.SPAWN))
            fph.addPlayerToFFA(p, ka, sa);
        else
        p.sendMessage(ChatColor.RED + "You can't load yourself to the main spawn");
    } 
}
