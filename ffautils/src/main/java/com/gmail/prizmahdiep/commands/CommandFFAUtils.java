package com.gmail.prizmahdiep.commands;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.BlockedCommandsManager;
import com.gmail.prizmahdiep.managers.DeathMessagesManager;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.RespawnItemManager;
import com.gmail.prizmahdiep.managers.SelectorManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.EditableSelectorInv;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.Selector;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.PlayerUtils;
import com.soundicly.jnanoidenhanced.jnanoid.NanoIdUtils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    private SelectorManager sem;
    private MiniMessage minimessage_deserializer;
    private RespawnItemManager rim;
    private DeathMessagesManager dmm;
    private BlockedCommandsManager bcm;

    public CommandFFAUtils
    (
        SpawnManager sp, 
        KitManager km, 
        FFAPlayersManager fph, 
        FFAUtils pl, 
        KitEditorManager kem, 
        SelectorManager sem, 
        MiniMessage minimessage_deserializer, 
        RespawnItemManager rim, 
        DeathMessagesManager dmm,
        BlockedCommandsManager bcm
    ) 
    {
        this.km = km;
        this.sm = sp;
        this.fph = fph;
        this.pl = pl;
        this.kem = kem;
        this.sem = sem;
        this.minimessage_deserializer = minimessage_deserializer;
        this.rim = rim;
        this.dmm = dmm;
        this.bcm = bcm;
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
        @CommandPermission("ffautils.admin.list.spawns")
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
        @CommandPermission("ffautils.admin.list.kits")
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

        @Subcommand("selectors")
        @CommandPermission("ffautils.admin.list.selectors")
        public void onListSelectors(CommandSender p)
        {
            p.sendMessage(ChatColor.AQUA + "Available selectors:");
            for (Selector i : sem.getSelectors().values())
                p.sendMessage(minimessage_deserializer.deserialize(i.getDisplayName() + "<yellow> - </yellow>" + i.getID()));
        }

        @Subcommand("ffaplayers")
        @CommandPermission("ffautils.admin.list.ffaplayers")
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
        @CommandPermission("ffautils.admin.create.kit")
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
                    if (km.createKit(kit_name.toUpperCase(), contents, pf, 
                                    Boolean.parseBoolean(restorable), 
                                    Boolean.parseBoolean(editable), kit_name))
                        p.sendMessage(ChatColor.AQUA + "Kit " + kit_name + " created");
                    else
                        p.sendMessage(ChatColor.RED + "Kit " + kit_name + " already exist");
                }
            }.runTaskAsynchronously(pl);
        }

        @Subcommand("spawn")
        @CommandCompletion("name " + SpawnLocation.SPAWN + "|" + SpawnLocation.STANDARD + "|" + SpawnLocation.EDITOR_ROOM + "|" + SpawnLocation.FTN)
        @CommandPermission("ffautils.admin.create.spawn")
        public void onCreateSpawn(Player p, String spawn_name, String type)
        {   
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (sm.createSpawn(spawn_name.toUpperCase(), type.toLowerCase(), spawn_name, "Click to teleport.", p.getLocation(), Material.GRAY_CONCRETE))
                        p.sendMessage(ChatColor.AQUA + "Spawn " + spawn_name + " created");
                    else
                        p.sendMessage(ChatColor.RED + "Could not create spawn " + spawn_name);
                }
            }.runTaskAsynchronously(pl);
        }

        @Subcommand("selector")
        @CommandCompletion("kit default_spawn zombie|husk|drowned|piglin|zombified_piglin|skeleton|wither_skeleton|stray|armor_stand")
        @CommandPermission("ffautils.admin.create.selector")
        public void onSelectorCreate(Player s, String kit, String default_spawn, String type)
        {
            Kit k = km.getKits().get(kit.toUpperCase());
            if (k == null) { s.sendMessage(ChatColor.RED + "Invalid kit"); return; }

            SpawnLocation ss = sm.getSpawns().get(default_spawn.toUpperCase());
            if (ss == null) { s.sendMessage(ChatColor.RED + "Invalid spawn"); return; }
            
            EntityType et = EntityType.fromName(type);
            if (et == null)
            {
                s.sendMessage(ChatColor.RED + "Entity type is not valid");
                return;
            }

            World wrld = s.getWorld();
            Location loc = s.getLocation();
            LivingEntity l = (LivingEntity) wrld.spawnEntity(loc, et);

            if (l instanceof ArmorStand) 
            {
                ((ArmorStand) l).setArms(true);
                ((ArmorStand) l).setBasePlate(false);
            }

            l.setCollidable(false);
            l.setSilent(true);
            l.setGravity(false);
            l.setRemoveWhenFarAway(false);
            l.setAI(false);
            l.setCustomNameVisible(true);
            l.setPersistent(true);

            EntityEquipment etq = l.getEquipment();
            etq.setArmorContents(k.getArmorContents());
            etq.setItemInMainHand(k.getMainhandItem());
            etq.setItemInOffHand(k.getOffhandItem());

            l.customName(minimessage_deserializer.deserialize("<!i>" + k.getDisplayName()));
            
            PersistentDataContainer epdc = l.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(pl, "selector-entity-type-id");
            Selector sel = new Selector(l, ss.getName(), k.getName(), k.getDisplayName(), "<red>Select a spawn</red>", NanoIdUtils.randomNanoId(5));

            sel.addSpawn(ss.getName());
            epdc.set(key, PersistentDataType.STRING, sel.getID());

            new BukkitRunnable() {
                @Override
                public void run()
                {
                    sem.addSelector(sel.getID(), sel);
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
        @CommandCompletion("spawn_name")
        @CommandPermission("ffautils.admin.remove.spawn")
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
        @CommandCompletion("kit_name")
        @CommandPermission("ffautils.admin.remove.kit")
        public void onKitRemove(CommandSender p, String kit_name)
        {
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (km.getKits().get(kit_name.toUpperCase()).isEditable())
                        kem.removeEditableKits(kit_name.toUpperCase());
                    if (km.removeKit(kit_name.toUpperCase()))
                        p.sendMessage(ChatColor.AQUA + "Kit " + kit_name + " deleted succesfully");
                    else
                        p.sendMessage(ChatColor.RED + "Kit " + kit_name + " does not exist");
                }
            }.runTaskAsynchronously(pl);
        }

        @Subcommand("selector")
        @CommandCompletion("id")
        @CommandPermission("ffautils.admin.remove.selector")
        public void onSelectorRemove(CommandSender p, String id)
        {
            if (!sem.getSelectors().containsKey(id))
            {
                p.sendMessage(ChatColor.RED + "This selector does not exist");
                return;
            }

            Selector s = sem.getSelectors().get(id);
            s.getEntity().remove();
            sem.removeSelector(id);
            p.sendMessage(ChatColor.AQUA + "Selector removed successfully");
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
        @CommandPermission("ffautils.admin.use.kit")
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
        @CommandPermission("ffautils.admin.use.spawn")
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
        @CommandPermission("ffautils.admin.reload.all")
        public void onDefault(CommandSender p)
        {
            p.sendMessage(ChatColor.AQUA + "Reloading config, kits, spawns and selectors");
            pl.reloadConfig();
            dmm.reload();
            rim.reload();
            bcm.reload();
            p.sendMessage(ChatColor.AQUA + "Config reloaded");
            for (Selector i : sem.getSelectors().values()) 
                if (i.getEntity() != null) 
                {   
                    i.getEntity().remove();
                    i.setEntity(null);
                }
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    int reloaded_kits = km.reloadKits();
                    int reloaded_spawns = sm.reloadSpawns();
                    int reloaded_selectors = sem.reloadSelectors();
                    p.sendMessage(ChatColor.AQUA + "" + reloaded_kits + " kits reloaded");
                    p.sendMessage(ChatColor.AQUA + "" + reloaded_spawns + " spawns reloaded");
                    new BukkitRunnable() {
                        @Override
                        public void run()
                        {
                            sem.getCachedInventories().clear();
                            for (Selector i : sem.getSelectors().values())
                            {
                                LivingEntity j = (LivingEntity) i.getEntity();
                                j.customName(minimessage_deserializer.deserialize(i.getDisplayName()));
                                EntityEquipment jeq = j.getEquipment();
                                Kit k = km.getKits().get(i.getKit());
                                jeq.setArmorContents(k.getArmorContents());
                                jeq.setItemInMainHand(k.getMainhandItem());
                                jeq.setItemInOffHand(k.getOffhandItem());
                            }
                        }
                    }.runTask(pl);
                    p.sendMessage(ChatColor.AQUA + "" + reloaded_selectors + " selectors reloaded");
                }
            }.runTaskAsynchronously(pl);
        }

        @Subcommand("kits")
        @CommandPermission("ffautils.admin.reload.kits")
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
        @CommandPermission("ffautils.admin.reload.spawns")
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

        @Subcommand("selectors")
        @CommandPermission("ffautils.admin.reload.selectors")
        public void onReloadSelectors(CommandSender p)
        {
            for (Selector i : sem.getSelectors().values()) 
                if (i.getEntity() != null) 
                {   
                    i.getEntity().remove();
                    i.setEntity(null);
                }
            
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    p.sendMessage(ChatColor.AQUA + "Reloading selectors");
                    int reloaded_selectors = sem.reloadSelectors();
                    p.sendMessage(ChatColor.AQUA + "" + reloaded_selectors + " selectors reloaded");

                    new BukkitRunnable() {
                        @Override
                        public void run()
                        {
                            sem.getCachedInventories().clear();
                            for (Selector i : sem.getSelectors().values())
                            {
                                LivingEntity j = (LivingEntity) i.getEntity();
                                j.customName(minimessage_deserializer.deserialize(i.getDisplayName()));
                                EntityEquipment jeq = j.getEquipment();
                                Kit k = km.getKits().get(i.getKit());
                                jeq.setArmorContents(k.getArmorContents());
                                jeq.setItemInMainHand(k.getMainhandItem());
                                jeq.setItemInOffHand(k.getOffhandItem());
                            }
                        }
                    }.runTask(pl);
                }
            }.runTaskAsynchronously(pl);
        }

        @Subcommand("config")
        @CommandPermission("ffautils.admin.reload.config")
        public void onReloadConfig(CommandSender p)
        {
            pl.reloadConfig();
            rim.reload();
            dmm.reload();
            bcm.reload();
            p.sendMessage(ChatColor.AQUA + "Config reloaded");
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
        @CommandPermission("ffautils.admin.loadall.loaded")
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
        @CommandPermission("ffautils.admin.loadall.unloaded")
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
        @CommandPermission("ffautils.admin.loadall.all")
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
    @CommandPermission("ffautils.admin.unloadall")
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
    @CommandPermission("ffautils.admin.load")
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
    @CommandPermission("ffautils.admin.unload")
    public void onUnloadPlayer(CommandSender s, String p)
    {
        SpawnLocation main_spawn = sm.getSpawnOfType(SpawnLocation.SPAWN);
        if (main_spawn == null)
        {
            s.sendMessage(ChatColor.RED + "There is not a main spawn defined");
            return;
        }

        FFAPlayer ffaplayer = fph.getFFAPlayers().get(Bukkit.getPlayer(p).getUniqueId());
        if (ffaplayer != null)  
        {
            fph.movePlayerFromFFA(ffaplayer);
            s.sendMessage(ChatColor.AQUA + "Unloading " + p + " from ffa");
        }
        else s.sendMessage(ChatColor.RED + p + " is not loaded");
    }

    @Subcommand("selector")
    @CommandPermission("ffautils.admin.selector")
    public class CommandSelector extends BaseCommand
    {
        @Subcommand("info")
        @CommandCompletion("id")
        @CommandPermission("ffautils.admin.selector.info")
        public void onSelectorInfo(CommandSender p, String id)
        {
            Selector s = sem.getSelectors().get(id);
            if (s == null)
            {
                p.sendMessage(ChatColor.RED + "This selector does not exist");
                return;
            }

            p.sendMessage(ChatColor.AQUA + "Selector " + ChatColor.WHITE + String.valueOf(id) + ":");
            p.sendMessage(minimessage_deserializer.deserialize("<gray>Display name: </gray>" + s.getDisplayName()));
            p.sendMessage(ChatColor.GRAY + "Entity type: " + ChatColor.WHITE + s.getEntity().getType().toString());
            p.sendMessage(ChatColor.GRAY + "Kit: " + ChatColor.WHITE + s.getKit());
            p.sendMessage(ChatColor.GRAY + "Default spawn: " + ChatColor.WHITE + s.getDefaultSpawn());

            String spawns = ChatColor.YELLOW + "{ " + ChatColor.WHITE;
            for (String i : s.getSpawns().keySet()) spawns += i + " ";
            spawns += ChatColor.YELLOW + "}";
            p.sendMessage(ChatColor.GRAY + "Spawns: " + spawns);
        }

        @Subcommand("addspawn")
        @CommandPermission("ffautils.admin.selector.addspawn")
        public void onAddSpawn(CommandSender p, String id, String spawn_name)
        {
            SpawnLocation s = sm.getSpawns().get(spawn_name.toUpperCase());
            if (s == null)
            {
                p.sendMessage(ChatColor.RED + "This spawn does not exist.");
                return;
            }

            Selector se = sem.getSelectors().get(id);

            if (se == null)
            {
                p.sendMessage(ChatColor.RED + "This selector does not exist.");
                return;
            }

            String spawn = s.getName();
            if (se.getSpawns().isEmpty()) se.setDefaultSpawn(spawn);
            se.addSpawn(spawn);
            sem.getCachedInventories().remove(id);
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    sem.reloadProperties(id);
                }
            }.runTaskAsynchronously(pl);
            p.sendMessage(ChatColor.AQUA + "Spawn " + spawn_name + " added to selector " + id);
        }

        @Subcommand("removespawn")
        @CommandCompletion("id spawn_name")
        @CommandPermission("ffautils.admin.selector.removespawn")
        public void onRemoveSpawn(CommandSender p, String id, String spawn_name)
        {
            SpawnLocation s = sm.getSpawns().get(spawn_name.toUpperCase());
            if (s == null)
            {
                p.sendMessage(ChatColor.RED + "This spawn does not exist.");
                return;
            }

            Selector se = sem.getSelectors().get(id);

            if (se == null)
            {
                p.sendMessage(ChatColor.RED + "This selector does not exist.");
                return;
            }

            se.removeSpawn(s.getName());
            if (se.getDefaultSpawn() != null)
                if (se.getDefaultSpawn().equals(s.getName())) se.setDefaultSpawn(null);
                
            sem.getCachedInventories().remove(id);
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    sem.reloadProperties(id);
                }
            }.runTaskAsynchronously(pl);
            p.sendMessage(ChatColor.AQUA + "Spawn " + spawn_name + " removed from selector " + id);
        }

        @Subcommand("editlayout")
        @CommandCompletion("id")
        @CommandPermission("ffautils.admin.selector.editlayout")
        public void onEditLayout(Player p, String id)
        {
            Selector sel = sem.getSelectors().get(id);
            
            if (sel == null)
            {
                p.sendMessage("This selector does not exist");
                return;
            }
            
            EditableSelectorInv inv = new EditableSelectorInv(pl, 9*3, id, "<red>Edit selector layout</red>", minimessage_deserializer);
            Map<String, Integer> spawns = sel.getSpawns();
            if (spawns.isEmpty())
            {
                p.sendMessage(ChatColor.RED + "This selector has no valid spawns associated");
                return;
            }

            p.sendMessage(ChatColor.AQUA + "Opening layout editor for selector " + id);
            Inventory contents = inv.getInventory();
            NamespacedKey key = new NamespacedKey(pl, "spawn-placeholder");

            for (String i : spawns.keySet())
            {
                ItemStack spawn_placeholder = new ItemStack(Material.PINK_CONCRETE);
                ItemMeta im = spawn_placeholder.getItemMeta();
                PersistentDataContainer pdc = im.getPersistentDataContainer();
                pdc.set(key, PersistentDataType.STRING, i);
                im.displayName(minimessage_deserializer.deserialize("<!i>" + "<blue>" + i + " placeholder</blue>"));
                spawn_placeholder.setItemMeta(im);

                contents.setItem(spawns.get(i), spawn_placeholder);
            } 

            p.openInventory(contents);
            p.sendMessage(ChatColor.AQUA + "This Layout will be saved when the inventory closes");
        }

        @Subcommand("tphere")
        @CommandCompletion("id")
        @CommandPermission("ffautils.admin.selector.tphere")
        public void onSelectorTPHere(Player p, String id)
        {
            Selector sel = sem.getSelectors().get(id);

            if (sel == null)
            {
                p.sendMessage("This selector does not exist");
                return;
            }

            sel.setLocation(p.getLocation());
            sel.getEntity().teleport(p.getLocation());

            new BukkitRunnable() {
                @Override
                public void run()
                {
                    sem.reloadProperties(sel.getID());
                    sem.reloadSelector(sel);
                }
            }.runTaskAsynchronously(pl);
            p.sendMessage(ChatColor.AQUA + "Selector moved correctly");
        }

        @Subcommand("defaultspawn")
        @CommandCompletion("id spawn")
        @CommandPermission("ffautils.admin.selector.defaultspawn")
        public void onSelectorSetDefaultSpawn(CommandSender sender, String id, String spawn)
        {
            Selector sel = sem.getSelectors().get(id);

            if (sel == null)
            {
                sender.sendMessage(ChatColor.RED + "This selector does not exist");
                return;
            }

            if (!sm.getSpawns().keySet().contains(spawn.toUpperCase()))
            {
                sender.sendMessage(ChatColor.RED + "This spawn does not exist");
                return;
            }

            String previous_spawn = sel.getDefaultSpawn();
            sel.setDefaultSpawn(spawn.toUpperCase());
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    sem.reloadProperties(id);
                    sem.reloadSelector(sel);
                }
            }.runTask(pl);
            sender.sendMessage(ChatColor.AQUA + "Default spawn changed from " + previous_spawn + " to " + spawn);
        }

        @Subcommand("kit")
        @CommandCompletion("id kit")
        @CommandPermission("ffautils.admin.selector.kit")
        public void onSelectorSetKit(CommandSender sender, String id, String kit)
        {
            Selector sel = sem.getSelectors().get(id);

            if (sel == null)
            {
                sender.sendMessage(ChatColor.RED + "This selector does not exist");
                return;
            }
            
            if (!km.getKits().keySet().contains(kit.toUpperCase()))
            {
                sender.sendMessage(ChatColor.RED + "This kit does not exist");
                return;
            }

            String previous_kit = sel.getKit();
            sel.setKit(kit.toUpperCase());
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    sem.reloadProperties(id);
                    sem.reloadSelector(sel);
                }
            }.runTask(pl);
            sender.sendMessage(ChatColor.AQUA + "Kit changed from " + previous_kit + " to " + kit);
        }

        @Subcommand("type")
        @CommandCompletion("id zombie|husk|drowned|piglin|zombified_piglin|skeleton|wither_skeleton|stray|armor_stand")
        @CommandPermission("ffautils.admin.selector.type")
        public void onSelectorSetEntityType(CommandSender s, String id, String type)
        {
            Selector sel = sem.getSelectors().get(id);
            if (sel == null)
            {
                s.sendMessage(ChatColor.RED + "This selector does not exist");
                return;
            }

            EntityType etype = EntityType.fromName(type);

            if (etype == null)
            {
                s.sendMessage(ChatColor.RED + "Entity type is not valid");
                return;
            }

            sel.setType(etype);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    sem.reloadProperties(id);
                    sem.reloadSelector(sel);
                }
            }.runTask(pl);
            s.sendMessage(ChatColor.AQUA + "Entity type set to " + type);
        }

        @Subcommand("displayname")
        @CommandCompletion("id minimessage")
        @CommandPermission("ffautils.admin.selector.displayname")
        public void onSelectorSetDisplayName(CommandSender s, String id, String minimsg)
        {
            Selector sel = sem.getSelectors().get(id);

            if (sel == null)
            {
                s.sendMessage(ChatColor.RED + "This selector does not exist");
                return;
            }

            sel.setDisplayName(minimsg);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    sem.reloadProperties(id);
                    sem.reloadSelector(sel);
                }
            }.runTask(pl);
            s.sendMessage(ChatColor.AQUA + "Display name changed successfully");
        }

        @Subcommand("containertitle")
        @CommandCompletion("id minimessage")
        @CommandPermission("ffautils.admin.selector.containertitle")
        public void onSelectorSetContainerTitle(CommandSender s, String id, String minimsg)
        {
            Selector sel = sem.getSelectors().get(id);

            if (sel == null)
            {
                s.sendMessage(ChatColor.RED + "This selector does not exist");
                return;
            }

            sel.setContainerTitle(minimsg);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    sem.reloadProperties(id);
                    sem.reloadSelector(sel);
                }
            }.runTask(pl);
            s.sendMessage(ChatColor.AQUA + "Container title changed successfully");
        }

        @Subcommand("replicate")
        @CommandCompletion("id")
        @CommandPermission("ffautils.admin.selector.replicate")
        public void onSelectorCopy(Player p, String id)
        {
            Selector se = sem.getSelectors().get(id);
            if (se == null)
            {
                p.sendMessage(ChatColor.RED + "This selector does not exist");
                return;
            }

            Selector ne = new Selector(se);
            String newid = NanoIdUtils.randomNanoId(5);
            ne.setID(newid);
            ne.setLocation(p.getLocation());
            sem.addSelector(newid, ne);
            sem.reloadSelector(ne);
            p.sendMessage(ChatColor.AQUA + "Selector replicated with ID " + newid);
        }
    }
}
