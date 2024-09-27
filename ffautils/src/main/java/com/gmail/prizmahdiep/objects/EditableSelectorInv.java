package com.gmail.prizmahdiep.objects;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.gmail.prizmahdiep.FFAUtils;

import net.kyori.adventure.text.minimessage.MiniMessage;
public class EditableSelectorInv implements InventoryHolder
{
    Inventory inv;
    int id;
    MiniMessage minimessage_deserializer;
    String title;

    public EditableSelectorInv(FFAUtils pl, int size, int id, String title, MiniMessage minimessage_deserializer)
    {
        this.title = title;
        this.minimessage_deserializer = minimessage_deserializer;
        this.inv = pl.getServer().createInventory(this, size, minimessage_deserializer.deserialize(title));
        this.id = id;
    }

    @Override
    public Inventory getInventory() 
    {
        return this.inv;
    }

    public int getSelectorID()
    {
        return this.id;
    }
}
