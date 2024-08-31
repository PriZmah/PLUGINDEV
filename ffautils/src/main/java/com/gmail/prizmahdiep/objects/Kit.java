package com.gmail.prizmahdiep.objects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class Kit
{ 
    private String name;
    private ItemStack[] inventory_contents;
    private boolean restorable = false;
    private boolean editable = false;
    private Collection<PotionEffect> effects;

    public Kit(String name, ItemStack[] inventory_contents, Collection<PotionEffect> effects, boolean restorable, boolean editable)
    {
        this.name = name;
        this.inventory_contents = inventory_contents;
        this.effects = effects;
        this.restorable = restorable;
        this.editable = editable;
    }

    public String getName()
    {
        return this.name;
    }

    public ItemStack[] getInventory()
    {
        return this.inventory_contents;
    }

    public Collection<PotionEffect> getPotionEffects()
    {
        return this.effects;
    }

    public boolean isRestorable()
    {
        return restorable;
    }

    public boolean isEditable()
    {
        return this.editable;
    }

    public void setRestorable(boolean restorable)
    {
        this.restorable = restorable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public void setContents(ItemStack[] contents)
    {
        this.inventory_contents = contents;
    }

    public void setEffects(Collection<PotionEffect> e)
    {
        this.effects = e;
    }
}
