package com.gmail.prizmahdiep.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class KitBase64Util 
{
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException
    {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream())
        {
            DataOutput output = new DataOutputStream(stream);
            output.writeInt(items.length);
            
            for (ItemStack i : items)
            {
                if (i == null || i.getType().equals(Material.AIR)) 
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

    public static ItemStack[] itemStackArrayFromBase64(String stream) throws IOException 
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

    public static String potionEffectCollectionToBase64(Collection<PotionEffect> potionEffects) throws IllegalStateException
    {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);)
        {		
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
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data)); BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);)
        {
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