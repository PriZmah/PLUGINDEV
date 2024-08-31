package com.gmail.prizmahdiep.commands;

import org.bukkit.entity.Player;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.SpawnLocation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("ffautils.unloadme")
@CommandAlias("unloadme|unldme|spawn|reset|k|die")
public class CommandUnloadme extends BaseCommand
{
    private FFAPlayersManager fph;
    private SpawnManager sm;

    public CommandUnloadme(FFAPlayersManager fph, SpawnManager sm)
    {
        this.sm = sm;
        this.fph = fph;
    }

    @Default
    public void onUnloadme(Player p)
    {
        FFAPlayer ffap = fph.getFFAPlayers().get(p.getUniqueId());
        SpawnLocation main_spawn = sm.getSpawnOfType(SpawnLocation.SPAWN);
        
        if (ffap == null)
        {
            p.sendMessage(ChatColor.RED + "You are not in FFA");
            return;
        }
        
        if (main_spawn == null)
        {
            p.sendMessage(ChatColor.RED + "Main spawn does not exist");
            return;
        }

        fph.movePlayerFromFFA(ffap);
    }
}
