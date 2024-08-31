package com.gmail.prizmahdiep.commands;

import org.bukkit.entity.Player;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.SpawnLocation;

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
    private FFAPlayersManager fph;
    private KitManager km;
    private SpawnManager sm;

    public CommandLoadme(FFAPlayersManager fph, KitManager km, SpawnManager sm)
    {
        this.fph = fph;
        this.km = km;
        this.sm = sm;
    }

    @Default
    @CommandCompletion("kit_name spawn_name")
    public void onLoadMe(Player p, String kit, String spawn)
    {
        SpawnLocation sa = sm.getSpawns().get(spawn.toUpperCase());
        Kit ka = km.getKits().get(kit.toUpperCase());

        if (fph.isOnFFA(p.getUniqueId()))
        {
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

        String satype = sa.getType();
        if (!satype.equals(SpawnLocation.SPAWN) && !satype.equals(SpawnLocation.EDITOR_ROOM) && !satype.equals(SpawnLocation.FTN))
            fph.addPlayerToFFA(p, ka, sa);
        else
            p.sendMessage(ChatColor.RED + "This spawn is not eligible for arbitrary use");
    } 
}
