package com.gmail.prizmahdiep.objects;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.gmail.prizmahdiep.FFAUtils;

import net.kyori.adventure.text.minimessage.MiniMessage;


public class SelectorInv implements InventoryHolder 
{
    private MiniMessage dse;
    private Inventory inv;
    private int id;

    public SelectorInv(FFAUtils pl, int size, int id, String title, MiniMessage dse)
    {
        this.dse = dse;
        this.inv = pl.getServer().createInventory(this, size, dse.deserialize(title));
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
