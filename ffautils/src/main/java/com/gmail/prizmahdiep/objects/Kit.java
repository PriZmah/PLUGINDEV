package com.gmail.prizmahdiep.objects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class Kit implements KitInterface
{ 
    protected String name;
    protected ItemStack[] inventory_contents;
    protected boolean restorable = false;
    protected Collection<PotionEffect> effects;

    public Kit(String name, ItemStack[] inventory_contents, Collection<PotionEffect> effects, boolean restorable)
    {
        this.name = name;
        this.inventory_contents = inventory_contents;
        this.effects = effects;
        this.restorable = restorable;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public ItemStack[] getInventory()
    {
        return this.inventory_contents;
    }

    @Override
    public Collection<PotionEffect> getPotionEffects()
    {
        return this.effects;
    }

    public boolean isRestorable()
    {
        return restorable;
    }
}
