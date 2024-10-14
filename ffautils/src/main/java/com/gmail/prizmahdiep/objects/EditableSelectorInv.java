package com.gmail.prizmahdiep.objects;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.gmail.prizmahdiep.FFAUtils;

import net.kyori.adventure.text.minimessage.MiniMessage;
public class EditableSelectorInv implements InventoryHolder
{
    private Inventory inv;
    private String id;
    private MiniMessage minimessage_deserializer;
    private String title;

    public EditableSelectorInv(FFAUtils pl, int size, String id, String title, MiniMessage minimessage_deserializer)
    {
        this.title = title;
        this.minimessage_deserializer = minimessage_deserializer;
        this.inv = pl.getServer().createInventory(this, size, this.minimessage_deserializer.deserialize(title));
        this.id = id;
    }

    @Override
    public Inventory getInventory() 
    {
        return this.inv;
    }

    public String getSelectorID()
    {
        return this.id;
    }

    public String getTitle()
    {
        return this.title;
    }
}
