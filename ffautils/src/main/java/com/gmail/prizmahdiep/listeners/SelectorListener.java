package com.gmail.prizmahdiep.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SelectorManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.EditableSelectorInv;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.Selector;
import com.gmail.prizmahdiep.objects.SelectorInv;
import com.gmail.prizmahdiep.objects.SpawnLocation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;

public class SelectorListener implements Listener
{
    private SelectorManager sem;
    private FFAPlayersManager fpm;
    private SpawnManager sm;
    private KitManager km;
    private FFAUtils pl;
    private MiniMessage mm;

    public SelectorListener(FFAUtils pl, SelectorManager sem, KitManager km, SpawnManager sm, FFAPlayersManager fpm, MiniMessage mm)
    {
        this.sem = sem;
        this.pl = pl;
        this.fpm = fpm;
        this.mm = mm;
        this.sm = sm;
        this.km = km;
    }

    @EventHandler
    public void onPlayerRightClickSelectorEvent(PlayerInteractAtEntityEvent e)
    {
        Entity en = e.getRightClicked();
        
        PersistentDataContainer pdc = en.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(pl, "selector-entity-type-id");
        if (!pdc.has(key, PersistentDataType.INTEGER)) return;
        e.setCancelled(true);
        
        Player p = e.getPlayer();
        if (fpm.isOnFFA(p.getUniqueId())) 
        {
            p.sendMessage(ChatColor.RED + "You are already in FFA");
            return;
        }

        int id = pdc.get(key, PersistentDataType.INTEGER);
        Inventory inv = null;
        if (sem.getCachedInventories().containsKey(id)) inv = sem.getCachedInventories().get(id);
        else
        {
            Selector sl = sem.getSelectors().get(id);
            SelectorInv sinv = new SelectorInv(pl, 27, sl.getID(), sl.getContainerName(), mm);
            inv = sinv.getInventory();
            Map<String, Integer> sps = sl.getSpawns();
            
            List<ItemStack> spawn_thumbnails = new ArrayList<>(sps.size());
            SpawnLocation curr;
            NamespacedKey key2 = new NamespacedKey(pl, "spawn-item-type");

            List<String> keyset = new ArrayList<>(sps.keySet());
            for (String i : keyset) 
            {
                curr = sm.getSpawns().get(i);

                if (curr == null) 
                { 
                    sl.removeSpawn(i);
                    if (sl.getDefaultSpawn().equals(i)) sl.setDefaultSpawn(null);
                    continue; 
                }

                Material thmb = curr.getThumbnail();
                ItemStack item_to_add = new ItemStack(thmb == null ? Material.GRAY_CONCRETE : thmb);
                spawn_thumbnails.add(item_to_add);

                ItemMeta mi = item_to_add.getItemMeta();

                mi.displayName(mm.deserialize("<!i>" + curr.getDisplayName()));

                List<Component> lores = new ArrayList<>();
                lores.add(mm.deserialize("<!i>" + curr.getLore()));

                mi.lore(lores);

                PersistentDataContainer ipdc = mi.getPersistentDataContainer();
                ipdc.set(key2, PersistentDataType.STRING, curr.getName());

                item_to_add.setItemMeta(mi);

                inv.setItem(sps.get(i), item_to_add);
            }
            Bukkit.getLogger().info("Caching selector inventory for selector " + id);
            sem.getCachedInventories().put(id, inv);
        }

        if (!inv.isEmpty()) p.openInventory(inv);
        else p.sendMessage(ChatColor.RED + "This selector has no spawns associated");
    }

    @EventHandler
    public void onPlayerLeftClickSelectorEvent(EntityDamageByEntityEvent e)
    {
        Entity attacker = e.getDamager();
        Entity victim = e.getEntity();

        if (!(attacker instanceof Player)) return;

        Player p = (Player) attacker;
        PersistentDataContainer pdc = victim.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(pl, "selector-entity-type-id");

        if (!pdc.has(key, PersistentDataType.INTEGER)) return;
        
        e.setCancelled(true);

        if (fpm.isOnFFA(p.getUniqueId())) 
        {
            p.sendMessage(ChatColor.RED + "You are already in FFA");
            return;
        }

        int id = pdc.get(key, PersistentDataType.INTEGER);
        Selector sel = sem.getSelectors().get(id);
        SpawnLocation sl = sm.getSpawns().get(sel.getDefaultSpawn());
        Kit k = km.getKits().get(sel.getKit());

        if (sl == null)
        {
            p.sendMessage(ChatColor.RED + "This spawn does not exist");
            sel.setDefaultSpawn(null);
            return;
        }

        if (k == null)
        {
            p.sendMessage(ChatColor.RED + "This kit does not exist");
            sel.setKit(null);
            return;
        }

        fpm.addPlayerToFFA(p, k, sl);
    }

    @EventHandler
    public void onSelectorTakeDamage(EntityDamageEvent e)
    {
        //if (e.getCause().equals(EntityDamageEvent.DamageCause.KILL)) return;
        NamespacedKey key = new NamespacedKey(pl, "selector-entity-type-id");
        if (e.getEntity().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) e.setCancelled(true);
    }

    @EventHandler
    public void onSelectorSetOnFire(EntityCombustEvent e)
    {
        NamespacedKey key = new NamespacedKey(pl, "selector-entity-type-id");
        if (e.getEntity().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSpawnMenuInteract(InventoryClickEvent e)
    {
        Inventory clicked_inventory = e.getClickedInventory();
        InventoryHolder inv_holder = clicked_inventory.getHolder(false);
        if (clicked_inventory == null || !(inv_holder instanceof SelectorInv)) return;
        
        e.setCancelled(true);

        SelectorInv selinv = (SelectorInv) inv_holder;
        HumanEntity hp = e.getWhoClicked();
        if (!(hp instanceof Player)) return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;

        PersistentDataContainer pdc = clicked.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(pl, "spawn-item-type");
        if (!pdc.has(key, PersistentDataType.STRING)) return;

        Player p = (Player) hp;
        Selector sl = sem.getSelectors().get(selinv.getSelectorID());

        String spawn_to_teleport = pdc.get(key, PersistentDataType.STRING);
        Kit kit_to_use = km.getKits().get(sl.getKit());
        SpawnLocation spawn_location = sm.getSpawns().get(spawn_to_teleport);

        if (kit_to_use == null)
        {
            p.sendMessage(ChatColor.RED + "This kit does not exist");
            sl.setKit(null);
            return;
        }

        if (spawn_location == null)
        {
            p.sendMessage(ChatColor.RED + "This spawn does not exist");
            sl.removeSpawn(spawn_to_teleport);
            sl.setDefaultSpawn(null);
            return;
        }

        fpm.addPlayerToFFA(p, kit_to_use, spawn_location);
    }

    @EventHandler
    public void onLayoutEditorClose(InventoryCloseEvent e)
    {
        HumanEntity human = e.getPlayer();
        if (!(human instanceof Player)) return;

        InventoryHolder h = e.getInventory().getHolder();
        if (!(h instanceof EditableSelectorInv)) return;

        EditableSelectorInv holder = (EditableSelectorInv) h;
        Selector sel = sem.getSelectors().get(holder.getSelectorID());
        Player p = (Player) human;
        Map<String, Integer> spawns = sel.getSpawns();
        Inventory inv = holder.getInventory();
        NamespacedKey key = new NamespacedKey(pl, "spawn-placeholder");

        for (int i = 0; i < inv.getSize(); i++)
        {
            ItemStack it = inv.getItem(i);
            if (it == null) continue;
            if (it.getType().equals(Material.AIR)) continue;

            PersistentDataContainer itm = it.getItemMeta().getPersistentDataContainer();
            if (!(itm.has(key, PersistentDataType.STRING))) return;

            String spawn_key = itm.get(key, PersistentDataType.STRING);
            if (spawns.containsKey(spawn_key))
                spawns.put(spawn_key, i);
        }
        new BukkitRunnable() {
            @Override
            public void run()
            {
                sem.reloadProperties(sel.getID());
            }
        }.runTaskAsynchronously(pl);
        sem.getCachedInventories().remove(sel.getID());
        p.sendMessage(ChatColor.AQUA + "Selector layout saved");
    }
}
