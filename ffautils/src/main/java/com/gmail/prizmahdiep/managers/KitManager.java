package com.gmail.prizmahdiep.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.SerializableKit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class KitManager 
{
    private Map<String, Kit> kits;
    private File kits_folder;
    private Gson gson;
    
    public KitManager(File kits_folder) throws IOException
    {
        this.kits_folder = kits_folder;
        if (!kits_folder.isDirectory()) throw new IOException("File is not a directory");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.kits = getKitsFromFiles();
    }

    public boolean createKit(String name, PlayerInventory inv, Collection<PotionEffect> pf, boolean restorable, boolean editable)
    {
        SerializableKit new_kit_serialized;
        ItemStack[] items = new ItemStack[41];
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < items.length; i++)
        {
            if (contents[i] == null || contents[i].getType().equals(Material.AIR)) continue;
            items[i] = new ItemStack(contents[i]);
        }

        new_kit_serialized = new SerializableKit(new Kit("", items, pf, restorable, editable));

        if (getKitFile(name) == null)
        {
            File kit_file = new File(kits_folder, name.toLowerCase() + ".json");
            try (FileWriter fw = new FileWriter(kit_file))
            {
                kit_file.createNewFile();
                gson.toJson(new_kit_serialized, fw);
                kits.put(name, new_kit_serialized.getKit(name));
            } 
            catch (JsonIOException | IOException e) 
            {
                e.printStackTrace();
            }
        } 
        else if (kits.containsKey(name)) return false;
        else kits.put(name, new_kit_serialized.getKit(name));
        return true;
    }

    public boolean removeKit(String name)
    {
        File kit_file = getKitFile(name);
        if (kits.containsKey(name)) kits.remove(name);
        if (kit_file != null) return kit_file.delete();
        return false;
    }

    private Map<String, Kit> getKitsFromFiles()
    {
        Map<String, Kit> kits = new HashMap<>();
        
        File[] kit_files = kits_folder.listFiles();
        for (File kit_file : kit_files)
            if (kit_file.isFile() && kit_file.getName().endsWith(".json"))
                try (BufferedReader br = new BufferedReader(new FileReader(kit_file))) 
                {
                    String name = FilenameUtils.removeExtension(kit_file.getName()).toUpperCase();
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) json.append(line);

                    kits.put(name, gson.fromJson(json.toString(), SerializableKit.class).getKit(name));
                } 
                catch (IOException | JsonSyntaxException e) 
                {
                    e.printStackTrace();
                }
        
        return kits;
    }

    private File getKitFile(String name)
    {
        File[] kit_files = kits_folder.listFiles();
        for (File kit_file : kit_files)
            if (kit_file.isFile())
                if (name.equalsIgnoreCase(FilenameUtils.removeExtension(kit_file.getName()))) return kit_file;
        
        return null;
    }

    public Map<String, Kit> getKits()
    {
        return this.kits;
    }

    public int reloadKits()
    {
        kits.clear();
        kits = getKitsFromFiles();
        return kits.size();
    }
}
