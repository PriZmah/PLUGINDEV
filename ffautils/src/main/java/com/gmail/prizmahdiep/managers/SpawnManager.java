package com.gmail.prizmahdiep.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import com.gmail.prizmahdiep.objects.SerializableSpawnLocation;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

public class SpawnManager 
{
    private Map<String, SpawnLocation> spawns;
    private File spawns_folder;
    private Gson gson;

    public SpawnManager(File spawns_folder) throws IOException
    {
        this.spawns_folder = spawns_folder;
        if (!spawns_folder.isDirectory()) throw new IOException("File is not a directory");
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.spawns = getSpawnsFromFiles();
    }

    public boolean createSpawn(String name, String type, String display_name, String lore, Location location, Material m)
    {
        SpawnLocation sa = new SpawnLocation("", location, type, display_name, lore, m);
        if (!isValidSpawn(sa)) return false;
        
        SerializableSpawnLocation spawn_serialized = new SerializableSpawnLocation(sa);
        if (getSpawnFile(name) == null)
        {
            File spawn_file = new File(spawns_folder, name.toLowerCase() + ".json");
            try (FileWriter fw = new FileWriter(spawn_file))
            {
                spawn_file.createNewFile();
                gson.toJson(spawn_serialized, fw);
                spawns.put(name, spawn_serialized.getSpawn(name));
            }
            catch (IOException | JsonIOException e)
            {
                e.printStackTrace();
            }
        }
        else if (spawns.containsKey(name)) return false;
        else spawns.put(name, spawn_serialized.getSpawn(name));
        return true;
    }

    public boolean removeSpawn(String name)
    {
        File df = getSpawnFile(name);
        if (spawns.containsKey(name)) spawns.remove(name);
        if (df != null) return df.delete();
        return false;
    }

    private Map<String, SpawnLocation> getSpawnsFromFiles() 
    {
        Map<String, SpawnLocation> spawns = new HashMap<>();
        File[] spawn_files = spawns_folder.listFiles();
        
        for (File spawn_file : spawn_files)
            if (spawn_file.isFile() && spawn_file.getName().endsWith(".json"))
                try (BufferedReader br = new BufferedReader(new FileReader(spawn_file)))
                {
                    String name = FilenameUtils.removeExtension(spawn_file.getName()).toUpperCase();
                    StringBuilder json = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) json.append(line);
                    
                    spawns.put(name, gson.fromJson(json.toString(), SerializableSpawnLocation.class).getSpawn(name));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

        return spawns;
    }

    private File getSpawnFile(String name) 
    {
        File[] files = spawns_folder.listFiles();
        for (File i : files)
            if (i.isFile())
                if (name.equalsIgnoreCase(FilenameUtils.removeExtension(i.getName()))) return i;
        return null;
    }

    public Map<String, SpawnLocation> getSpawns()
    {
        return this.spawns;
    }

    public int reloadSpawns()
    {
        spawns.clear();
        spawns = getSpawnsFromFiles();
        return spawns.size();
    }

    public SpawnLocation getSpawnOfType(String type)
    {
        for (SpawnLocation i : spawns.values())
            if (i.getType().equals(type)) return i;
        
        return null;
    }

    public boolean isValidSpawn(SpawnLocation spawn)
    {
        if (spawn == null) return false;
        if (spawn.getLocation().getWorld() == null) return false;
        String spawntype = spawn.getType();
        if (!spawntype.equals(SpawnLocation.EDITOR_ROOM) && !spawntype.equals(SpawnLocation.FTN)
        && !spawntype.equals(SpawnLocation.SPAWN) && !spawntype.equals(SpawnLocation.STANDARD)) return false;

        for (SpawnLocation i : spawns.values())
            if (spawn.getType().equals(i.getType()) && i.getType().equals(SpawnLocation.SPAWN)
                && !spawn.equals(i)) return false;
        return true;
    }
}
