package com.gmail.prizmahdiep.commands;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.PlayerUtils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("futils|ffa|ffautils")
@CommandPermission("ffautils.admin")
public class CommandFFAUtils extends BaseCommand
{  
    private SpawnManager sm;
    private KitManager km;
    private FFAPlayersManager fph;
    private FFAUtils pl;
    private KitEditorManager kem;

    public CommandFFAUtils(SpawnManager sp, KitManager km, FFAPlayersManager fph, FFAUtils pl, KitEditorManager kem) 
    {
        this.km = km;
        this.sm = sp;
        this.fph = fph;
        this.pl = pl;
        this.kem = kem;
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
            sm.getSpawns().forEach((spawn_name, spawn) ->
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
            km.getKits().forEach((name, kit) ->
                p.sendMessage(name + 
                    (kit.isRestorable() ? ChatColor.YELLOW + " - " + ChatColor.GRAY + "restorable" : "") +
                    (kit.isEditable() ? ChatColor.YELLOW + " - " + ChatColor.GRAY + "editable" : "")
                )
            );
        }

        @Subcommand("ffaplayers")
        public void onListFFAPlayers(CommandSender p)
        {
            p.sendMessage(ChatColor.AQUA + "Active FFA Players:");
            
            fph.getFFAPlayers().forEach((uuid, ffaplayer) ->
                p.sendMessage(
                    Bukkit.getPlayer(uuid).getName() 
                    + ChatColor.YELLOW + " - " 
                    + ChatColor.GRAY + ffaplayer.getCurrentPlayerKitName()
                    + ChatColor.DARK_GRAY + "(" + ffaplayer.getLastPlayerKitName() + ")"
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
        @CommandCompletion("name true|false")
        public void onCreateKit(Player p, String kit_name, @Optional @Default("false") String restorable, @Optional @Default("false") String editable)
        {
            PlayerInventory contents = p.getInventory();
            Collection<PotionEffect> pf = p.getActivePotionEffects();
        
            if (kit_name.equals("none"))
            {
                p.sendMessage(ChatColor.RED + "This name is reserved");
                return;
            }

            if (contents.isEmpty() && pf.isEmpty())
            {
                p.sendMessage(ChatColor.RED + "Cannot create an empty kit");
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (km.createKit(kit_name.toUpperCase(), contents, pf, Boolean.parseBoolean(restorable), Boolean.parseBoolean(editable)))
                        p.sendMessage(ChatColor.AQUA + "Kit " + kit_name + " created");
                    else
                        p.sendMessage(ChatColor.RED + "Kit " + kit_name + " already exist");
                }
            }.runTaskAsynchronously(pl);
        }

        @Subcommand("spawn")
        @CommandCompletion("name " + SpawnLocation.SPAWN + "|" + SpawnLocation.STANDARD + "|" + SpawnLocation.EDITOR_ROOM + "|" + SpawnLocation.FTN)
        public void onCreateSpawn(Player p, String spawn_name, String type)
        {   
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (sm.createSpawn(spawn_name.toUpperCase(), type.toLowerCase(), p.getLocation()))
                        p.sendMessage(ChatColor.AQUA + "Spawn " + spawn_name + " created");
                    else
                        p.sendMessage(ChatColor.RED + "Could not create spawn " + spawn_name);
                }
            }.runTaskAsynchronously(pl);
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
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (sm.removeSpawn(spawn_name.toUpperCase()))
                        p.sendMessage(ChatColor.AQUA + "Spawn " + spawn_name + " deleted succesfully");
                    else
                        p.sendMessage(ChatColor.RED + "Spawn " + spawn_name + " does not exist");
                }
            }.runTaskAsynchronously(pl);
        }

        @Subcommand("kit")
        public void onKitRemove(CommandSender p, String kit_name)
        {
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (km.getKits().get(kit_name.toUpperCase()).isEditable())
                    {
                        kem.removeEditableKit(kit_name.toUpperCase());
                    }
                    if (km.removeKit(kit_name.toUpperCase()))
                        p.sendMessage(ChatColor.AQUA + "Kit " + kit_name + " deleted succesfully");
                    else
                        p.sendMessage(ChatColor.RED + "Kit " + kit_name + " does not exist");
                }
            }.runTaskAsynchronously(pl);
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
            Kit pk = km.getKits().get(kit_name.toUpperCase());
            Player pt = null;
            if (pk == null)
            {
                p.sendMessage(ChatColor.RED + "Kit " + kit_name + " does not exist");
                return;
            }

            if (other_p != null)
            {
                pt = Bukkit.getPlayer(other_p);
                if (pt == null) p.sendMessage(ChatColor.RED + "Player not found");
            }
            else if (p instanceof Player) pt = (Player) p;
            else 
            {
                p.sendMessage(ChatColor.RED + "Bad command syntax for non-player executor");
                return;
            }

            PlayerUtils.setPlayerKit(pt, pk);
            p.sendMessage(ChatColor.AQUA + "Kit " + kit_name + " loaded to " + pt.getName());
        }

        @Subcommand("spawn")
        public void onUseSpawn(CommandSender p, String spawn_name, @Optional String other_p)
        {
            SpawnLocation spawn_to_teleport = sm.getSpawns().get(spawn_name.toUpperCase());
            Player p_target;
            
            if (other_p == null)
            {
                if (p instanceof Player) p_target = (Player) p;
                else
                {
                    p.sendMessage(ChatColor.RED + "Bad command syntax for non-player executor");
                    return;
                }
            }
            else p_target = Bukkit.getPlayer(other_p);

            if (p_target == null)
            {
                p.sendMessage(ChatColor.RED + "Player not found");
                return;
            }

            if (PlayerUtils.teleportPlayerToSpawn(p_target, spawn_to_teleport))
                p.sendMessage(ChatColor.AQUA + p_target.getName() + " teleported to " + spawn_name);
            else
                p.sendMessage(ChatColor.RED + "Spawn " + spawn_name + " could not be used");
        }
    }

    @Subcommand("reload")
    @CommandPermission("ffautils.admin.reload")
    public class CommandReload extends BaseCommand
    {
        @Default
        public void onDefault(CommandSender p)
        {
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    p.sendMessage(ChatColor.AQUA + "Reloading Kits and Spawns");
                    int reloaded_kits = km.reloadKits();
                    int reloaded_spawns = sm.reloadSpawns();
                    p.sendMessage(ChatColor.AQUA + "" + reloaded_kits + " kits reloaded");
                    p.sendMessage(ChatColor.AQUA + "" + reloaded_spawns + " spawns reloaded");
                }
            }.runTaskAsynchronously(pl);
            // lo otro
        }

        @Subcommand("kits")
        public void onReloadKits(CommandSender p)
        {
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    p.sendMessage(ChatColor.AQUA + "Reloading Kits");
                    int reloaded_kits = km.reloadKits();
                    p.sendMessage(ChatColor.AQUA + "" + reloaded_kits + " kits reloaded");
                }
            }.runTaskAsynchronously(pl);
        }

        @Subcommand("spawns")
        public void onReloadSpawns(CommandSender p)
        {
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    p.sendMessage(ChatColor.AQUA + "Reloading Spawns");
                    int reloaded_spawns = sm.reloadSpawns();
                    p.sendMessage(ChatColor.AQUA + "" + reloaded_spawns + " spawns reloaded");
                }
            }.runTaskAsynchronously(pl);
        }
    }

    @Subcommand("loadall")
    @CommandPermission("ffautils.admin.loadall")
    public class CommandLoadAll extends BaseCommand
    {
        private Map<String, SpawnLocation> spawns = sm.getSpawns();
        private Map<String, Kit> kits = km.getKits();

        @Subcommand("loaded")
        @CommandCompletion("kit_name spawn_name")
        public void onLoadAllLoadedPlayers(CommandSender s, String kit, String spawn)
        {
            SpawnLocation spawn_to_teleport = spawns.get(spawn.toUpperCase());
            Kit k = kits.get(kit.toUpperCase());
            
            if (k == null)
            {
                s.sendMessage(ChatColor.RED + "Kit " + kit + " does not exist");
                return;
            }
            
            if (!sm.isValidSpawn(spawn_to_teleport))
            {
                s.sendMessage(ChatColor.RED + "Invalid spawn");
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run()
                {
                    fph.getFFAPlayers().forEach(
                        (uuid, f) ->
                        {
                            String ck = f.getCurrentPlayerKitName();
                            f.setCurrentPlayerKitName(kit.toUpperCase());
                            f.setLastPlayerKitName(ck);

                            String cs = f.getCurrentSpawnName();
                            f.setCurrentSpawnName(spawn.toUpperCase());
                            f.setLastSpawnName(cs);

                            PlayerUtils.setPlayerKit(f.getPlayer(), k);
                            PlayerUtils.teleportPlayerToSpawn(f.getPlayer(), spawn_to_teleport);
                        }
                    );
                }
            }.runTaskAsynchronously(pl);

            s.sendMessage(ChatColor.AQUA + "Reloading all ffa players");
        }

        @Subcommand("unloaded")
        @CommandCompletion("kit_name spawn_name")
        public void onLoadAllUnloadedPlayers(CommandSender s, String kit, String spawn)
        {
            SpawnLocation spawn_to_teleport = spawns.get(spawn.toUpperCase());
            Kit k = kits.get(kit.toUpperCase());
            
            if (k == null)
            {
                s.sendMessage(ChatColor.RED + "Kit " + kit + " does not exist");
                return;
            }
            
            if (!sm.isValidSpawn(spawn_to_teleport))
            {
                s.sendMessage(ChatColor.RED + "Invalid spawn");
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run()
                {
                    Bukkit.getOnlinePlayers().forEach(
                        (p) ->  fph.addPlayerToFFA(p, k, spawn_to_teleport)
                    );
                }
            }.runTaskAsynchronously(pl);

            s.sendMessage(ChatColor.AQUA + "Loading all unloaded players");
        }

        @Subcommand("all")
        @CommandCompletion("kit_name spawn_name")
        public void onLoadAllPlayers(CommandSender s, String kit, String spawn)
        {
            SpawnLocation spawn_to_teleport = spawns.get(spawn.toUpperCase());
            Kit k = kits.get(kit.toUpperCase());
            
            if (k == null)
            {
                s.sendMessage(ChatColor.RED + "Kit " + kit + " does not exist");
                return;
            }
            
            if (!sm.isValidSpawn(spawn_to_teleport))
            {
                s.sendMessage(ChatColor.RED + "Invalid spawn");
                return;
            }
            
            new BukkitRunnable() {
                @Override
                public void run()
                { 
                    Bukkit.getOnlinePlayers().forEach(
                        (p) ->  
                        {   
                            FFAPlayer ffaplayer;
                            if (!fph.getFFAPlayers().containsKey(p.getUniqueId()))
                            {
                                ffaplayer = new FFAPlayer(p, k.getName(), spawn_to_teleport.getName());
                                fph.addPlayerToFFA(ffaplayer.getPlayer(), k, spawn_to_teleport);
                            } 
                            else
                            {
                                ffaplayer = fph.getFFAPlayers().get(p.getUniqueId());
                                PlayerUtils.teleportPlayerToSpawn(ffaplayer.getPlayer(), spawn_to_teleport);
                                PlayerUtils.setPlayerKit(ffaplayer.getPlayer(), k);
                            }
                        }
                    );
                }
            }.runTaskAsynchronously(pl);

            s.sendMessage(ChatColor.AQUA + "Loading all players");
        }
    }

    @Subcommand("unloadall")
    public void onUnloadall(CommandSender s)
    {
        SpawnLocation main_spawn = sm.getSpawnOfType(SpawnLocation.SPAWN);
        if (main_spawn == null)
        {
            s.sendMessage(ChatColor.RED + "There is not a main spawn defined");
            return;
        }

        FFAPlayer ffaplayer;
        int set_size = fph.getFFAPlayers().keySet().size();
        UUID[] player_keys = fph.getFFAPlayers().keySet().toArray(new UUID[set_size]);
        
        for (int i = 0; i < set_size; i++)
        {
            ffaplayer = fph.getFFAPlayers().get(player_keys[i]);
            fph.movePlayerFromFFA(ffaplayer);
        }

        s.sendMessage(ChatColor.AQUA + "Unloading all players");
    }

    @Subcommand("load")
    public void onLoadPlayer(CommandSender s, String p, String kit, String spawn)
    {
        Player pj = Bukkit.getPlayer(p);
        Kit k = km.getKits().get(kit.toUpperCase());
        SpawnLocation sp = sm.getSpawns().get(spawn.toUpperCase());

        if (pj != null && k != null && sp != null)
        {
            if (fph.isOnFFA(pj.getUniqueId()))
                fph.removePlayerFromFFA(fph.getFFAPlayers().get(pj.getUniqueId()));
            fph.addPlayerToFFA(pj, k, sp);
            s.sendMessage(ChatColor.AQUA + "Loading " + p + " to ffa");
        } else s.sendMessage(ChatColor.AQUA + "Couldn't load " + p + " to ffa");
    }

    @Subcommand("unload")
    public void onUnloadPlayer(CommandSender s, String p)
    {
        SpawnLocation main_spawn = sm.getSpawnOfType(SpawnLocation.SPAWN);
        if (main_spawn == null)
        {
            s.sendMessage(ChatColor.RED + "There is not a main spawn defined");
            return;
        }

        FFAPlayer ffaplayer = fph.getFFAPlayers().get(Bukkit.getPlayer(p).getUniqueId());
        if (ffaplayer != null)  fph.movePlayerFromFFA(ffaplayer);
        
        s.sendMessage(ChatColor.AQUA + "Unloading " + p + " from ffa");
    }
}
