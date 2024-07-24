package com.gmail.prizmahdiep.objects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class PlayerKit 
{ 
    private String name;
    private ItemStack[] inventory_contents;
    private Collection<PotionEffect> effects;

    public PlayerKit(String name, ItemStack[] inventory_contents, Collection<PotionEffect> effects)
    {
        this.name = name;
        this.inventory_contents = inventory_contents;
        this.effects = effects;
    }

    public String getName()
    {
        return this.name;
    }

    public ItemStack[] getInventoryContents()
    {
        return this.inventory_contents;
    }

    public Collection<PotionEffect> getEffects()
    {
        return this.effects;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setInventory(ItemStack[] inventory)
    {
        this.inventory_contents = inventory;
    }

    public void setPotionEffects(Collection<PotionEffect> effects)
    {
        this.effects = effects;
    }
}
