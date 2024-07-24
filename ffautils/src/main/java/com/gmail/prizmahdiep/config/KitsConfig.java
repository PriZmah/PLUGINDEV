package com.gmail.prizmahdiep.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.ByteArrayInputStream;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.DataOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.objects.PlayerKit;


public class KitsConfig 
{
    private final String config_filename = "kits.yml";
    private FFAUtils pl;
    private File kits_configuration_file;
    private FileConfiguration kits_configuration;

    public KitsConfig(FFAUtils ffa_utils) 
    {
        this.pl = ffa_utils;
    }
    
    public void save()
    {
        try 
        {
            getKitsConfiguration().save(kits_configuration_file);    
        } 
        catch (IOException e) 
        {
            pl.getLogger().warning("Unable to save " + config_filename);
        }
    }

    public FileConfiguration getKitsConfiguration()
    {
        return this.kits_configuration;
    }

    public void createKitsConfiguration() 
    {
        kits_configuration_file = new File(pl.getDataFolder(), config_filename);
        if (!kits_configuration_file.exists())
        {
            try 
            {
                kits_configuration_file.createNewFile();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        kits_configuration = new YamlConfiguration();
        try 
        {
            kits_configuration.load(kits_configuration_file);
        } 
        catch (IOException | InvalidConfigurationException e) 
        {
            e.printStackTrace();
        }
    }
    
    public boolean createKitConfigEntry(PlayerKit pk)
    {
        if (kits_configuration.contains(pk.getName()))
        {
            pl.getLogger().info("Kit " + pk.getName() + " already exists");
            return false;
        }

        String base64inventory = itemStackArrayToBase64(pk.getInventoryContents());
        String base64effects = potionEffectArrayToBase64(pk.getEffects());
        
        kits_configuration.set(pk.getName() + ".inventory", base64inventory);
        kits_configuration.set(pk.getName() + ".effects", base64effects);

        save();
        pl.getLogger().info("Kit " + pk.getName() + " created.");
        return true;
    }

    
    public boolean removeKitConfigEntry(String name)
    {
        if (!kits_configuration.contains(name))
        {
            pl.getLogger().info("Kit " + name + " does not exists");
            return false;
        }

        kits_configuration.set(name, null);        

        save();
        pl.getLogger().info("Kit " + name + " deleted.");
        return true;
    }

    public Map<String, PlayerKit> getKitObjects() 
    {
        try 
        { kits_configuration.load(kits_configuration_file); } 
        catch (IOException | InvalidConfigurationException e) 
        { e.printStackTrace(); }

        Map<String, PlayerKit> ret = new HashMap<>();

        PlayerKit current;
        for (String kit_name : kits_configuration.getKeys(false))
        {
            current = new PlayerKit("foo", null, null);
            current.setName(kit_name);
            try
            {
                current.setInventory(
                    itemStackArrayFromBase64(
                        kits_configuration.getString(kit_name + ".inventory")
                    )
                );
                        
                current.setPotionEffects(
                    potionEffectsFromBase64(
                        kits_configuration.getString(kit_name + ".effects")
                    )
                );
            }
            catch (IOException e)
            {
                pl.getLogger().warning("Could not get " + kit_name + " kit object");
                break;      
            }
                 
            ret.put(kit_name, current);
        }
        
        return ret;
    }
    
    private String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException
    {
        try 
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutput output = new DataOutputStream(stream);

            output.writeInt(items.length);
            
            for (ItemStack i : items)
            {
                if (i == null) 
                {
                    output.writeInt(0);
                    continue;
                }

                byte[] bytes = i.serializeAsBytes();
                output.writeInt(bytes.length);
                output.write(bytes);
            }

            return Base64Coder.encodeLines(stream.toByteArray());
        } 
        catch (Exception e) 
        {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    private ItemStack[] itemStackArrayFromBase64(String stream) throws IOException 
    {
        byte[] bytes = Base64Coder.decodeLines(stream);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes))
        {
            DataInputStream input = new DataInputStream(inputStream);
            int count = input.readInt();
            ItemStack[] items = new ItemStack[count];
            for (int i = 0; i < count; i++) {
                int length = input.readInt();
                if (length == 0) continue;
                byte[] itemBytes = new byte[length];
                input.read(itemBytes);
                items[i] = ItemStack.deserializeBytes(itemBytes);
            }
            return items;
        } 
        catch (Exception e) 
        {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    private String potionEffectArrayToBase64(Collection<PotionEffect> potionEffects) throws IllegalStateException
    {
        try 
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			
			dataOutput.writeInt(potionEffects.size());
			
			for ( Iterator<PotionEffect> i = potionEffects.iterator(); i.hasNext(); )
				dataOutput.writeObject(i.next());
			
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
        } 
        catch (Exception e) 
        {
            throw new IllegalStateException("Unable to save potion effect.", e);
        }
    }

    public static Collection<PotionEffect> potionEffectsFromBase64(String data) throws IOException
    {
		try 
        {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			Collection<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
			int length = dataInput.readInt();
			
			for (int i = 0; i < length; i++)
				potionEffects.add((PotionEffect) dataInput.readObject());
			
			dataInput.close();
			return potionEffects;
		} 
        catch (ClassNotFoundException | IOException e) 
        {
            throw new IOException("Unable to decode class type.", e);
		}
	}
}
