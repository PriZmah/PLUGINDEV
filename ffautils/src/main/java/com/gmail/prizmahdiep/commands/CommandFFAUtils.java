package com.gmail.prizmahdiep.commands;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

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
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("futils|ffa|ffautils")
@CommandPermission("ffautils.admin")
public class CommandFFAUtils extends BaseCommand
{  
    private SpawnUtils sp;
    private KitUtils ku;
    private FFAPlayersHandler fph;

    public CommandFFAUtils(SpawnUtils sp, KitUtils ku, FFAPlayersHandler fph) 
    {
        this.sp = sp;
        this.ku = ku;
        this.fph = fph;
    }

    @Default
    public void onDefault(CommandSender p)
    {
        p.sendMessage(ChatColor.YELLOW + "Please specify a subcommand");
    }

    @Subcommand("list")
    @CommandPermission("ffautils.admin.list")
    public class CommandList extends BaseCommand
    {
        @Default
        public void onDefault(CommandSender p)
        {
            p.sendMessage(ChatColor.RED + "Bad arguments");
        }

        @Subcommand("spawns")
        public void onListSpawns(CommandSender p)
        {
            p.sendMessage(ChatColor.AQUA + "Available spawns:");
            SpawnUtils.spawns.forEach((spawn_name, spawn) ->
                {
                    ChatColor color_type;
                    String spawn_type = spawn.getType();
                    if (spawn_type.equals(SpawnLocation.SPAWN)) color_type = ChatColor.AQUA;
                    else color_type = ChatColor.GRAY;
                    p.sendMessage(spawn_name + ChatColor.YELLOW + " - " + color_type + spawn_type);
                } 
            );
        }

        @Subcommand("kits")
        public void onListKits(CommandSender p)
        {
            p.sendMessage(ChatColor.AQUA + "Available kits:");
            ku.getExistingKits().forEach(
                    (kit) -> p.sendMessage(kit)
            );
        }

        @Subcommand("ffaplayers")
        public void onListFFAPlayers(CommandSender p)
        {
            p.sendMessage(ChatColor.AQUA + "Active FFA Players:");
            FFAPlayersHandler.ffa_players.forEach((uuid, ffaplayer) ->
                p.sendMessage(
                    Bukkit.getPlayer(uuid).getName() 
                    + ChatColor.YELLOW + " - " 
                    + ChatColor.GRAY + ffaplayer.getPlayerKit().getName()
                    + ChatColor.DARK_GRAY + "(" + ffaplayer.getLastPlayerKit().getName() + ")"
                )
            );
        }
    }

    @Subcommand("create")
    @CommandPermission("ffautils.admin.create")
    public class CommandCreate extends BaseCommand
    {
        @Default
        public void onDefault(Player p)
        {
            p.sendMessage(ChatColor.RED + "Bad arguments");
        }

        @Subcommand("kit")
        @Syntax("<kit_name>")
        public void onCreateKit(Player p, String kit_name)
        {
            PlayerInventory pinv = p.getInventory();
            if (pinv.isEmpty())
            {
                p.sendMessage(ChatColor.RED + "Cannot create an empty kit");
                return;
            }

            ItemStack[] items = pinv.getContents();
            Collection<PotionEffect> pf = p.getActivePotionEffects();


            if (ku.createKit(kit_name.toUpperCase(), items, pf))
                p.sendMessage(ChatColor.AQUA + "Kit " + kit_name + " created");
            else
                p.sendMessage(ChatColor.RED + "Kit " + kit_name + " already exist");
        }

        @Subcommand("spawn")
        @Syntax("<spawn_name> <type>")
        @CommandCompletion("foo STANDARD|SPAWN")
        public void onCreateSpawn(Player p, String spawn_name, String type)
        {   
            if (sp.createSpawn(spawn_name.toUpperCase(), p.getLocation(), type.toLowerCase()))
                p.sendMessage(ChatColor.AQUA + "Spawn " + spawn_name + " created");
            else
                p.sendMessage(ChatColor.RED + "This spawn already exist");
        }
    }

    @Subcommand("remove")
    @CommandPermission("ffautils.admin.remove")
    public class CommandRemove extends BaseCommand
    {
        @Default
        public void onDefault(CommandSender p)
        {
            p.sendMessage(ChatColor.RED + "Bad arguments");
        }

        @Subcommand("spawn")
        public void onSpawnRemove(CommandSender p, String spawn_name)
        {  
            if (sp.removeSpawn(spawn_name.toUpperCase()))
                p.sendMessage(ChatColor.AQUA + "Spawn " + spawn_name + " deleted succesfully");
            else
                p.sendMessage(ChatColor.RED + "Spawn " + spawn_name + " does not exist");
        }

        @Subcommand("kit")
        public void onKitRemove(CommandSender p, String kit_name)
        {
            if (ku.removeKit(kit_name.toUpperCase()))
                p.sendMessage(ChatColor.AQUA + "Kit " + kit_name + " deleted succesfully");
            else
                p.sendMessage(ChatColor.RED + "Kit " + kit_name + " does not exist");
        }
    }

    @Subcommand("use")
    @CommandPermission("ffautils.admin.use")
    public class CommandUse extends BaseCommand
    {
        @Default
        public void onDefault(CommandSender p)
        {
            p.sendMessage(ChatColor.RED + "Choose either a kit or a spawn");
        }

        @Subcommand("kit")
        public void onUseKit(CommandSender p, String kit_name, @Optional String other_p)
        {
            if (other_p != null)
            {
                Player k = Bukkit.getPlayer(other_p);
                if (k != null)
                {
                    if (ku.setPlayerKit(kit_name.toUpperCase(), k))
                        p.sendMessage(ChatColor.AQUA + "Kit " + kit_name + " loaded to " + k.getName());
                    else
                        p.sendMessage(ChatColor.RED + "Kit " + kit_name + " does not exist");
                } else p.sendMessage(ChatColor.RED + "Player not found");
            }
            else if (p instanceof Player)
            {
                if (ku.setPlayerKit(kit_name.toUpperCase(), (Player) p))
                    p.sendMessage(ChatColor.AQUA + "Kit " + kit_name + " loaded");
                else
                    p.sendMessage(ChatColor.RED + "Kit " + kit_name + " does not exist");
            }
            else p.sendMessage(ChatColor.RED + "Bad command syntax for non-player executor");
        }

        @Subcommand("spawn")
        public void onUseSpawn(CommandSender p, String spawn_name, @Optional String other_p)
        {
            if (other_p != null)
            {
                Player k = Bukkit.getPlayer(other_p);
                if (k != null)
                {
                    if (sp.teleportEntityToSpawn(spawn_name.toUpperCase(), k))
                        p.sendMessage(ChatColor.AQUA + k.getName() + " teleported to " + spawn_name);
                    else
                        p.sendMessage(ChatColor.RED + "Spawn " + spawn_name + " does not exist");
                }
                else p.sendMessage(ChatColor.RED + "Player not found");
            }
            else if (p instanceof Player)
            {
                if (sp.teleportEntityToSpawn(spawn_name.toUpperCase(), (Player) p))
                    p.sendMessage(ChatColor.AQUA + "Teleported to " + spawn_name);
                else
                    p.sendMessage(ChatColor.RED + "Spawn " + spawn_name + " does not exist");
            } else p.sendMessage(ChatColor.RED + "Bad command syntax for non-player executor");
        }
    }

    @Subcommand("reload")
    @CommandPermission("ffautils.admin.reload")
    public class CommandReload extends BaseCommand
    {
        @Default
        public void onDefault(CommandSender p)
        {
            p.sendMessage(ChatColor.AQUA + "Reloading Kits and Spawns");
            int reloaded_kits = ku.reloadKits();
            int reloaded_spawns = sp.reloadSpawns();
            p.sendMessage(ChatColor.AQUA + "" + reloaded_kits + " kits reloaded");
            p.sendMessage(ChatColor.AQUA + "" + reloaded_spawns + " spawns reloaded");
            // lo otro
        }

        @Subcommand("kits")
        public void onReloadKits(CommandSender p)
        {
            p.sendMessage(ChatColor.AQUA + "Reloading Kits");
            int reloaded_kits = ku.reloadKits();
            p.sendMessage(ChatColor.AQUA + "" + reloaded_kits + " kits reloaded");
        }

        @Subcommand("spawns")
        public void onReloadSpawns(CommandSender p)
        {
            p.sendMessage(ChatColor.AQUA + "Reloading Spawns");
            int reloaded_spawns = sp.reloadSpawns();
            p.sendMessage(ChatColor.AQUA + "" + reloaded_spawns + " spawns reloaded");
        }
    }

    @Subcommand("loadall")
    @CommandPermission("ffautils.admin.loadall")
    public class CommandLoadAll extends BaseCommand
    {
        @Subcommand("loaded")
        @CommandCompletion("kit_name spawn_name")
        public void onLoadAllLoadedPlayers(CommandSender s, String kit, String spawn)
        {
            if (!KitUtils.kits.containsKey(kit.toUpperCase()))
            {
                s.sendMessage(ChatColor.RED + "Kit " + kit + " does not exist");
                return;
            }
            
            if (!SpawnUtils.spawns.containsKey(spawn.toUpperCase()))
            {
                s.sendMessage(ChatColor.RED + "Spawn " + spawn + " does not exist");
                return;
            }
            
            if (SpawnUtils.spawns.get(spawn.toUpperCase()).getType().equals(SpawnLocation.SPAWN))
            {
                s.sendMessage(ChatColor.RED + "You can't load to the main spawn");
                return;
            }

            PlayerKit k = KitUtils.kits.get(kit.toUpperCase());
            SpawnLocation spl = SpawnUtils.spawns.get(spawn.toUpperCase());
            FFAPlayersHandler.ffa_players.forEach(
                (uuid, ffaplayer) ->
                {
                    ffaplayer.setPlayerKit(k);
                    ffaplayer.setPlayerSpawn(spl);
                    ku.setPlayerKit(k.getName(), ffaplayer.getPlayer());
                    sp.teleportEntityToSpawn(spawn.toUpperCase(), ffaplayer.getPlayer());
                }
            );
        }

        @Subcommand("unloaded")
        @CommandCompletion("kit_name spawn_name")
        public void onLoadAllUnloadedPlayers(CommandSender s, String kit, String spawn)
        {
            if (!KitUtils.kits.containsKey(kit.toUpperCase()))
            {
                s.sendMessage(ChatColor.RED + "Kit " + kit + " does not exist");
                return;
            }

            if (!SpawnUtils.spawns.containsKey(spawn.toUpperCase()))
            {
                s.sendMessage(ChatColor.RED + "Spawn " + spawn + " does not exist");
                return;
            }

            if (SpawnUtils.spawns.get(spawn.toUpperCase()).getType().equals(SpawnLocation.SPAWN))
            {
                s.sendMessage(ChatColor.RED + "You can't load to the main spawn");
                return;
            }

            PlayerKit k = KitUtils.kits.get(kit.toUpperCase());
            SpawnLocation spl = SpawnUtils.spawns.get(spawn.toUpperCase());

            Bukkit.getOnlinePlayers().forEach(
                (p) ->  fph.addPlayerToFFA(p, k, spl)
            );
        }

        @Subcommand("all")
        @CommandCompletion("kit_name spawn_name")
        public void onLoadAllPlayers(CommandSender s, String kit, String spawn)
        {
            if (!KitUtils.kits.containsKey(kit.toUpperCase()))
            {
                s.sendMessage(ChatColor.RED + "Kit " + kit + " does not exist");
                return;
            }

            if (!SpawnUtils.spawns.containsKey(spawn.toUpperCase()))
            {
                s.sendMessage(ChatColor.RED + "Spawn " + spawn + " does not exist");
                return;
            }

            if (SpawnUtils.spawns.get(spawn.toUpperCase()).getType().equals(SpawnLocation.SPAWN))
            {
                s.sendMessage(ChatColor.RED + "You can't load to the main spawn");
                return;
            }

            PlayerKit k = KitUtils.kits.get(kit.toUpperCase());
            SpawnLocation spl = SpawnUtils.spawns.get(spawn.toUpperCase());
            
            Bukkit.getOnlinePlayers().forEach(
                (p) ->  
                {   
                    FFAPlayer ffaplayer;
                    if (!FFAPlayersHandler.ffa_players.containsKey(p.getUniqueId()))
                    {
                        ffaplayer = new FFAPlayer(p, k, spl);
                        ffaplayer.setPlayerKit(k);
                        ffaplayer.setPlayerSpawn(spl);
                        fph.addPlayerToFFA(ffaplayer.getPlayer(), k, spl);
                    } 
                    else
                    {
                        ffaplayer = FFAPlayersHandler.ffa_players.get(p.getUniqueId());
                        ffaplayer.setPlayerKit(k);
                        ffaplayer.setPlayerSpawn(spl);
                        ku.setPlayerKit(k.getName(), ffaplayer.getPlayer());
                        sp.teleportEntityToSpawn(spawn.toUpperCase(), ffaplayer.getPlayer());
                    }
                }
            );
        }
    }

    @Subcommand("unloadall")
    public void onUnloadall(CommandSender s)
    {
        SpawnLocation main_spawn = sp.getMainSpawn();
        if (sp.getMainSpawn() == null)
        {
            s.sendMessage(ChatColor.RED + "There is not a main spawn defined");
            return;
        }

        FFAPlayer ffaplayer;
        for (UUID i : FFAPlayersHandler.ffa_players.keySet())
        {
            ffaplayer = FFAPlayersHandler.ffa_players.get(i);

            ffaplayer.setPlayerKit(null);
            ffaplayer.setPlayerKit(null);
    
            ffaplayer.setPlayerSpawn(main_spawn);
            ffaplayer.setPlayerSpawn(main_spawn);
    
            fph.removePlayerFromFFA(ffaplayer.getPlayer());
        }
    }
}
