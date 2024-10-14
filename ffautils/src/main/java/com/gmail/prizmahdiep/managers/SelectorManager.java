package com.gmail.prizmahdiep.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.Selector;
import com.gmail.prizmahdiep.objects.SerializableSelector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.kyori.adventure.text.minimessage.MiniMessage;

import org.apache.commons.io.FilenameUtils;

public class SelectorManager
{
    private Map<String, Selector> selectors;
    private Map<String, Inventory> cached_inventories;
    private MiniMessage minimessage_deserializer;
    private File selectors_folder;
    private Gson gson;
    private KitManager kitman;
    private FFAUtils pl;

    public SelectorManager(File selectors_folder, MiniMessage mm, KitManager km, FFAUtils pl) throws IOException
    {
        this.selectors_folder = selectors_folder;
        if (!selectors_folder.isDirectory()) throw new IOException("File is not a directory");
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.selectors = getSelectorsFromFiles();
        this.cached_inventories = new HashMap<>();
        this.minimessage_deserializer = mm;
        this.kitman = km;
        this.pl = pl;

        for (Selector i : selectors.values())
        {
            reloadSelector(i);
        }
    }

    private Map<String, Selector> getSelectorsFromFiles() 
    {
        Map<String, Selector> selectors = new HashMap<>();
        File[] selector_files = selectors_folder.listFiles();
        for (File selector_file : selector_files)
            if (selector_file.isFile() && selector_file.getName().endsWith(".json"))
            {
                try (BufferedReader br = new BufferedReader(new FileReader(selector_file))) 
                {
                    StringBuilder json = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) json.append(line);
                    SerializableSelector sel = gson.fromJson(json.toString(), SerializableSelector.class);
                    Selector s = sel.getSelector();
                    selectors.put(s.getID(), s);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }

            }
        
        return selectors;
    }

    public Map<String, Selector> getSelectors()
    {
        return this.selectors;
    }
    
    public Map<String, Inventory> getCachedInventories()
    {
        return this.cached_inventories;
    }

    public boolean addSelector(String k, Selector v)
    {
        SerializableSelector ser = new SerializableSelector(v);
        if (getSelectorFile(String.valueOf(v.getID())) == null)
        {
            File selector_file = new File(selectors_folder, String.valueOf(v.getID() + ".json"));
            try (FileWriter fw = new FileWriter(selector_file))
            {
                selector_file.createNewFile();
                gson.toJson(ser, fw);
                selectors.put(k, v);
            }
            catch (IOException | JsonIOException e)
            {
                e.printStackTrace();
            }
        }
        else if (selectors.containsKey(v.getID())) return false;
        else selectors.put(k, v);
        return true;
    }

    public boolean removeSelector(String k)
    {
        File df = getSelectorFile(String.valueOf(k));
        if (selectors.containsKey(k)) selectors.remove(k);
        if (df != null) return df.delete();
        return false;
    }

    private File getSelectorFile(String filename) 
    {
        File[] files = selectors_folder.listFiles();
        for (File i : files)
            if (i.isFile())
                if (filename.equalsIgnoreCase(FilenameUtils.removeExtension(i.getName()))) return i;
        return null;
    }


    public int reloadSelectors() 
    {
        selectors.clear();
        selectors = getSelectorsFromFiles();
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Selector i : selectors.values()) reloadSelector(i);
            }
        }.runTask(pl);
        return selectors.size();
    }
    
    public void reloadSelector(Selector i) 
    {
        Location loc = i.getLocation();
        if (loc == null) return;

        if (!loc.getChunk().isLoaded())
            loc.getChunk().load();
    
        LivingEntity l = (LivingEntity) loc.getWorld().spawnEntity(loc, i.getType());
    
        l.customName(minimessage_deserializer.deserialize(i.getDisplayName()));
        EntityEquipment jeq = l.getEquipment();
        Kit k = kitman.getKits().get(i.getKit());
        if (k != null) 
        {
            jeq.setArmorContents(k.getArmorContents());
            jeq.setItemInMainHand(k.getMainhandItem());
            jeq.setItemInOffHand(k.getOffhandItem());
        }
    
        
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
    
        PersistentDataContainer epdc = l.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(pl, "selector-entity-type-id");
        epdc.set(key, PersistentDataType.STRING, i.getID());
        
        if (i.getEntity() != null) i.getEntity().remove();
        i.setEntity(l);
    }

    public void reloadProperties(String id) 
    {
        File sel_file = getSelectorFile(String.valueOf(id));
        if (sel_file == null) return;
        Selector sel = selectors.get(id);
        SerializableSelector sela = new SerializableSelector(sel);
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(sel_file)))
        {
            gson.toJson(sela, bw);
        } 
        catch (IOException | JsonSyntaxException e) 
        {
            e.printStackTrace();
        }
    }
}