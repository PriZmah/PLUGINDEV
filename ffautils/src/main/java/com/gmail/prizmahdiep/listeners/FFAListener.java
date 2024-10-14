package com.gmail.prizmahdiep.listeners;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.events.FFAPlayerLoadEvent;
import com.gmail.prizmahdiep.events.FFAPlayerUnloadEvent;
import com.gmail.prizmahdiep.managers.BlockedCommandsManager;
import com.gmail.prizmahdiep.managers.CombatTagManager;
import com.gmail.prizmahdiep.managers.DeathMessagesManager;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.RespawnItemManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.PlayerUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;

public class FFAListener implements Listener
{
    private SpawnManager sm;
    private KitManager km;
    private FFAPlayersManager fph;
    private KitEditorManager kem;
    private FFAUtils futils;
    private CombatTagManager ctm;
    private RespawnItemManager rim;
    private DeathMessagesManager dmm;
    private BlockedCommandsManager bcm;
    private MiniMessage mmd;

    public FFAListener
    (
        SpawnManager su, 
        KitManager km, 
        FFAPlayersManager fph, 
        FFAUtils futils, 
        KitEditorManager kem, 
        CombatTagManager ctm, 
        RespawnItemManager rim, 
        DeathMessagesManager dmm, 
        MiniMessage mmd,
        BlockedCommandsManager bcm
    )
    {
        this.sm = su;
        this.km = km;
        this.fph = fph;
        this.futils = futils;
        this.kem = kem;
        this.ctm = ctm;
        this.rim = rim;
        this.dmm = dmm;
        this.bcm = bcm;
        this.mmd = mmd;
    }

    @EventHandler
    public void onFFAPlayerLoad(FFAPlayerLoadEvent event) 
    {
        FFAPlayer fp = event.getFFAPlayer();
        Player p = fp.getPlayer();
        Kit ki = km.getKits().get(fp.getCurrentPlayerKitName());
        SpawnLocation li = sm.getSpawns().get(fp.getCurrentSpawnName());

        if (ki != null && li != null)
        {
            PlayerUtils.teleportPlayerToSpawn(p, li);
            PlayerUtils.resetPlayerStatus(p);
            PlayerUtils.setPlayerKit(p, ki);

            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (ki.isEditable() && kem.EditedKitExists(p.getUniqueId(), ki.getName()))
                    {
                        ItemStack[] k = kem.getEditedKit(p.getUniqueId(), ki.getName());
                        new BukkitRunnable() 
                        {
                            @Override
                            public void run() 
                            {
                                p.getInventory().setContents(k);
                            }
                        }.runTask(futils);
                    }
                    
                }
            }.runTaskAsynchronously(futils);
        } else p.sendMessage(ChatColor.RED + "Either the kit or the spawn is not valid.");
    }

    @EventHandler
    public void onPlayerUnload(FFAPlayerUnloadEvent p)
    {
        FFAPlayer pf = p.getFFAPlayer();
        Player pi = pf.getPlayer();
        
        PlayerUtils.teleportPlayerToSpawn(pi, sm.getSpawnOfType(SpawnLocation.SPAWN));
        pi.getInventory().clear();
        pi.clearActivePotionEffects();
        PlayerUtils.resetPlayerStatus(pi);

        if (!fph.isIdle(pf.getUUID())) return;
        if (!futils.getConfig().getBoolean("fast-respawn-item")) return;

        pf.getPlayer().getInventory().setItem(4, rim.getFastRespawnItem());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev)
    {
        Player victim = ev.getEntity();
        Player killer = ev.getEntity().getKiller();
        
        double max_health = 0;
        double current_health = 0;
        double needed_health = 0;
        boolean flag = false;
        
        if (fph.isOnFFA(victim.getUniqueId()))
            { ev.getDrops().clear(); flag = true; ctm.removeTag(victim.getUniqueId()); }
        
        if (killer == null)
        {
            if (!flag) return;
            FFAPlayer fvic = fph.getFFAPlayers().get(victim.getUniqueId());
            Player pa = Bukkit.getPlayer(fvic.getOpponent());
            if (pa != null) killer = pa;
            else return;
            fvic.setOpponent(null);
        }

        max_health = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        current_health = killer.getHealth();
        needed_health = max_health - current_health;

        if (fph.isOnFFA(killer.getUniqueId()))
        {
            List<String> death_messages = dmm.getDeathMessages();
            String death_message = death_messages.get(new Random().nextInt(death_messages.size()));

            Component death_message_final = dmm.parseDeathMessage(victim, killer, current_health, death_message);
            
            ev.deathMessage(death_message_final);
            killer.setHealth(current_health + needed_health);
            killer.sendActionBar(Component.text(ChatColor.GREEN + "Healed " + (double) Math.round(needed_health * 100) / 100 + " HP"));
            
            FFAPlayer fp = fph.getFFAPlayers().get(killer.getUniqueId());
            Kit ki = km.getKits().get(fp.getCurrentPlayerKitName());
            if (ki != null)
                if (ki.isRestorable())
                    PlayerUtils.restorePlayerKit(fp);
            fp.setOpponent(null);
            ctm.removeTag(killer.getUniqueId());
            killer.sendMessage(ChatColor.GREEN + "You are no longer in combat");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent ev)
    {   
        ev.setRespawnLocation(sm.getSpawnOfType(SpawnLocation.SPAWN).getLocation());
        Map<UUID, FFAPlayer> plrs = fph.getFFAPlayers();
        FFAPlayer pluid = plrs.get(ev.getPlayer().getUniqueId());

        if (pluid != null)
            new BukkitRunnable() {
                @Override
                public void run() 
                {
                    PlayerUtils.resetFFAPlayer(pluid);
                }
            }.runTaskLater(futils, 1);
    }

    
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e)
    {
        Entity edmg = e.getDamager();
        Entity evic = e.getEntity();
        
        if (!(edmg instanceof Player) || !(evic instanceof Player)) return;
        
        Player atk = (Player) edmg;
        Player vic = (Player) evic;
        
        combatLog(atk, vic);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e)
    {
        ProjectileSource shtr = e.getEntity().getShooter();
        Entity a = e.getHitEntity();

        if (!(shtr instanceof Player) || !(a instanceof Player)) return;
        
        Player atk = (Player) shtr;
        Player vic = (Player) a;

        combatLog(atk, vic);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent ev)
    {
        if (!futils.getConfig().getBoolean("fast-respawn-item")) return;

        ItemStack item = ev.getItem();
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return;
        
        UUID p = ev.getPlayer().getUniqueId();
        ItemMeta item_meta = ev.getItem().getItemMeta();
    
        if (item_meta == null) return;
        
        NamespacedKey key = new NamespacedKey(futils, "respawn-item-type");
        FFAPlayer fp;
        if (item_meta.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN))
        {
            ev.setCancelled(true);
            Map<String, Kit> kia = km.getKits();
            Map<String, SpawnLocation> sia = sm.getSpawns();
    
            if (fph.isIdle(p) && !fph.isOnFFA(p))
            {   
                fp = fph.getIdleFFAPlayers().get(p);
                Kit ck = kia.get(fp.getCurrentPlayerKitName());
                SpawnLocation cs = sia.get(fp.getCurrentSpawnName());
                
                if (ck == null || cs == null) return;
                fph.addPlayerToFFA(fp.getPlayer(), ck, cs);
            }
            else Bukkit.getPlayer(p).sendMessage(ChatColor.RED + "You were not in FFA");
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e)
    {
        Player p = e.getPlayer();
        if (!ctm.isTagged(p.getUniqueId())) return;
        if (p.hasPermission("ffautils.loadme.bypasscombatlog")) return;

        String comm = e.getMessage().toLowerCase();
        Set<String> blocked_commands = bcm.getBlockedCommands();
        String[] comm_parts = comm.split(":");
        String base_comm = comm_parts[comm_parts.length - 1].split(" ")[0];
        
        for (String i : blocked_commands)
        {
            if (base_comm.equals("/" + i) || base_comm.equals(i))
            {
                e.setCancelled(true);
                p.sendMessage(ChatColor.RED + "This command is disabled when you're in combat");
                return;
            }
        }
    }

    public void combatLog(Player atk, Player vic)
    {
        if (fph.isOnFFA(atk.getUniqueId()))
        {
            FFAPlayer ffatk = fph.getFFAPlayers().get(atk.getUniqueId());
            if (!ffatk.hasOpponent())
            ffatk.setOpponent(vic.getUniqueId());
            
            UUID fatkuid =  ffatk.getUUID();
            
            if (ctm.isTagged(fatkuid)) ctm.removeTag(fatkuid);
            else atk.sendMessage(ChatColor.RED + "You are now in combat");
            ctm.setTag(ffatk.getUUID(), Duration.ofSeconds(CombatTagManager.DEFAULT_COOLDOWN));
        }

        if (fph.isOnFFA(vic.getUniqueId()))
        {
            FFAPlayer ffavic = fph.getFFAPlayers().get(vic.getUniqueId());
            if (!ffavic.hasOpponent())
                ffavic.setOpponent(atk.getUniqueId());

            UUID fvicuid =  ffavic.getUUID();
            
            if (ctm.isTagged(fvicuid)) ctm.removeTag(fvicuid);
            else vic.sendMessage(ChatColor.RED + "You are now in combat");
            ctm.setTag(ffavic.getUUID(), Duration.ofSeconds(CombatTagManager.DEFAULT_COOLDOWN));
        }
    }

}

