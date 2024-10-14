package com.gmail.prizmahdiep.objects;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.gmail.prizmahdiep.FFAUtils;

import net.kyori.adventure.text.minimessage.MiniMessage;


public class SelectorInv implements InventoryHolder 
{
    private MiniMessage dse;
    private Inventory inv;
    private String id;

    public SelectorInv(FFAUtils pl, int size, String id, String title, MiniMessage dse)
    {
        this.dse = dse;
        this.inv = pl.getServer().createInventory(this, size, this.dse.deserialize(title));
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
}
