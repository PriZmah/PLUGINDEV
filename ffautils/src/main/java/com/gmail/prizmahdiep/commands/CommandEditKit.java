package com.gmail.prizmahdiep.commands;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.objects.Kit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("editkit|ek")
@CommandPermission("ffautils.editkit")
public class CommandEditKit extends BaseCommand
{
    private KitManager km;
    private KitEditorManager kem;
    private FFAUtils pl;
    private FFAPlayersManager fph;

    public CommandEditKit(KitManager km, KitEditorManager kem, FFAUtils pl, FFAPlayersManager fph)
    {
        this.km = km;
        this.kem = kem;
        this.pl = pl;
        this.fph = fph;
    }

    @Default
    public void onKitEdit(Player p, String kit_name)
    {
        UUID po = p.getUniqueId();
        Kit kit_to_edit = km.getKits().get(kit_name.toUpperCase());
        
        if (kem.EditedKitExists(po, kit_name))
            kit_to_edit.setContents(kem.getEditedKit(po, kit_name));
        
        if (kit_to_edit == null) 
        {
            p.sendMessage(ChatColor.RED + "This kit does not exist");
            return;
        }
        if (kem.getPlayers().get(po) != null)
        {
            p.sendMessage(ChatColor.RED + "You are already in the kit editor.");
            return;
        }

        if (fph.getFFAPlayers().get(po) != null)
        {
            p.sendMessage(ChatColor.RED + "You can't edit a kit from here.");
            return;
        }

        if (kit_to_edit.isEditable())
            kem.load_player(p.getUniqueId(), kit_to_edit);
        else p.sendMessage(ChatColor.RED + "This kit is not editable");
    }

    @Subcommand("exit")
    public void onKitEditCancel(Player p)
    {
        if (kem.getPlayers().get(p.getUniqueId()) != null) kem.unload_player(p.getUniqueId());
        else p.sendMessage(ChatColor.RED + "You are not in the kit editor");
    }

    @Subcommand("save")
    public void onKitEditSave(Player p)
    {
        UUID puid = p.getUniqueId();
        Kit k = kem.getPlayers().get(puid);
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
}
