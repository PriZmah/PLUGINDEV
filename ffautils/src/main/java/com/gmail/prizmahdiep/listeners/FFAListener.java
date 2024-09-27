package com.gmail.prizmahdiep.listeners;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.events.FFAPlayerLoadEvent;
import com.gmail.prizmahdiep.events.FFAPlayerUnloadEvent;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.PlayerUtils;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class FFAListener implements Listener
{
    private SpawnManager sm;
    private KitManager km;
    private FFAPlayersManager fph;
    private KitEditorManager kem;
    private FFAUtils futils;

    public FFAListener(SpawnManager su, KitManager km, FFAPlayersManager fph, FFAUtils futils, KitEditorManager kem)
    {
        this.sm = su;
        this.km = km;
        this.fph = fph;
        this.futils = futils;
        this.kem = kem;
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

        ItemStack respawn_item = PlayerUtils.getRespawnItem();

        pf.getPlayer().getInventory().setItem(4, respawn_item);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev)
    {
        Player victim = ev.getEntity();
        Player killer = ev.getEntity().getKiller();
        
        if (victim.getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK))
        {
            double max_health = 0;
            double current_health = 0;
            double needed_health = 0;
    
            max_health = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            current_health = killer.getHealth();
            needed_health = max_health - current_health;

            if (fph.isOnFFA(victim.getUniqueId()))
                ev.getDrops().clear();
            
            
            if (fph.isOnFFA(killer.getUniqueId()))
            {
                ev.deathMessage(Component.text(ChatColor.RED + "☠ " + victim.getName() + ChatColor.GRAY + " fue asesinado por " 
                + ChatColor.GREEN + killer.getName() + ChatColor.GRAY + " con " + ChatColor.WHITE + String.valueOf((double) Math.round(current_health * 100) / 100) + 
                ChatColor.YELLOW + " ❤"));
                killer.setHealth(current_health + needed_health);
                killer.sendActionBar(Component.text(ChatColor.GREEN + "Healed " + (double) Math.round(needed_health * 100) / 100 + " HP"));
                
                FFAPlayer fp = fph.getFFAPlayers().get(killer.getUniqueId());
                Kit ki = km.getKits().get(fp.getCurrentPlayerKitName());
                if (ki != null)
                    if (ki.isRestorable())
                        PlayerUtils.restorePlayerKit(fp);
            }
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
    public void onPlayerInteract(PlayerInteractEvent ev)
    {
        ItemStack item = ev.getItem();
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return;
        
        UUID p = ev.getPlayer().getUniqueId();
        ItemMeta item_meta = ev.getItem().getItemMeta();

        if (item_meta == null) return;
        
        NamespacedKey key = new NamespacedKey(futils, "respawn-item-type");
        FFAPlayer fp;
        if (item_meta.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN))
        {
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
}

