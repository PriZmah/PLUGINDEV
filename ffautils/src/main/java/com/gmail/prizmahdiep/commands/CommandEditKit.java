package com.gmail.prizmahdiep.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.SpawnLocation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("kiteditor|ke")
@CommandPermission("ffautils.kiteditor")
public class CommandEditKit extends BaseCommand
{
    private KitManager km;
    private KitEditorManager kem;
    private FFAUtils pl;
    private FFAPlayersManager fph;
    private SpawnManager sm;

    public CommandEditKit(KitManager km, KitEditorManager kem, FFAUtils pl, FFAPlayersManager fph, SpawnManager sm)
    {
        this.km = km;
        this.kem = kem;
        this.pl = pl;
        this.fph = fph;
        this.sm = sm;
    }

    @Default
    public void onKitEdit(Player p, String kit_name)
    {
        UUID po = p.getUniqueId();
        Kit kit_to_edit = km.getKits().get(kit_name.toUpperCase());
        
        if (kit_to_edit == null) 
        {
            p.sendMessage(ChatColor.RED + "This kit does not exist");
            return;
        }
        if (kem.getKitEditorPlayers().get(po) != null)
        {
            p.sendMessage(ChatColor.RED + "You are already in the kit editor.");
            return;
        }

        if (fph.getFFAPlayers().get(po) != null)
        {
            p.sendMessage(ChatColor.RED + "You can't edit a kit from here.");
            return;
        }

        if (sm.getSpawnOfType(SpawnLocation.EDITOR_ROOM) == null)
        {
            p.sendMessage(ChatColor.RED + "The editor room is not available");
            return;
        }
        
        if (kit_to_edit.isEditable())
            kem.load_player(p.getUniqueId(), kit_to_edit);
        else p.sendMessage(ChatColor.RED + "This kit is not editable");
    }

    @Subcommand("exit")
    @CommandPermission("ffautils.kiteditor.exit")
    public void onKitEditCancel(Player p)
    {
        if (kem.getKitEditorPlayers().get(p.getUniqueId()) != null) kem.unload_player(p.getUniqueId());
        else p.sendMessage(ChatColor.RED + "You are not in the kit editor");
    }

    @Subcommand("save")
    @CommandPermission("ffautils.kiteditor.save")
    public void onKitEditSave(Player p)
    {
        UUID puid = p.getUniqueId();
        Kit k = kem.getKitEditorPlayers().get(puid);
        if (k != null)
        {
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (kem.saveEditedKit(puid, p.getInventory().getContents(), k.getName()))
                        p.sendMessage(ChatColor.AQUA + "Kit saved");
                    else p.sendMessage(ChatColor.AQUA + "Kit Overwritten");
                }
            }.runTaskAsynchronously(pl);
        }
        else p.sendMessage(ChatColor.RED + "You are not in the kit editor");
    }

    @Subcommand("restore")
    @CommandPermission("ffautils.kiteditor.restore")
    public void onEditedKitRestore(Player p, String kit_name)
    {
        UUID puid = p.getUniqueId();
        if (kem.EditedKitExists(puid, kit_name.toUpperCase()))
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    kem.removeEditableKits(puid, kit_name.toUpperCase());
                    p.sendMessage(ChatColor.AQUA + "Kit restored to its original state");
                }
            }.runTaskAsynchronously(pl);
        else p.sendMessage(ChatColor.RED + "This kit does not exist or you haven't edited it yet");
    }

    @Subcommand("clearkits")
    @CommandPermission("ffautils.kiteditor.clearkits")
    public void onEditedKitsClear(Player p)
    {
        new BukkitRunnable() {
            @Override
            public void run()
            {
                kem.removeEditableKits(p.getUniqueId());
                p.sendMessage(ChatColor.AQUA + "All kits cleared");
            }
        }.runTaskAsynchronously(pl);
    }

    @Subcommand("list")
    @CommandPermission("ffautils.kiteditor.list")
    public void onEditedKitsList(Player p)
    {
        p.sendMessage(ChatColor.AQUA + "Edited kits list:");
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                List<String> l = kem.getEditedKitNames(p.getUniqueId());
                for (String i : l)
                    p.sendMessage(ChatColor.WHITE + i);
            }
        }.runTaskAsynchronously(pl);
    }
}
