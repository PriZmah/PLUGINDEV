package com.gmail.prizmahdiep.objects;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class EditableKit implements KitInterface
{
    private UUID owner;
    private String name;
    private ItemStack[] inventory;
    private Collection<PotionEffect> effects;
    private boolean restorable;

    public EditableKit(String name, ItemStack[] inventory_contents, Collection<PotionEffect> effects, boolean restorable, UUID owner) 
    {
        this.name = name;
        this.inventory = inventory_contents;
        this.effects = effects;
        this.owner = owner;
        this.restorable = restorable;
    }

    public UUID getOwner()
    {
        return owner;
    }

    @Override
    public ItemStack[] getInventory() 
    {
        return this.inventory;
    }

    @Override
    public Collection<PotionEffect> getPotionEffects() 
    {
        return this.effects;
    }

    @Override
    public String getName() 
    {
        return this.name;
    }

    @Override
    public boolean isRestorable()
    {
        return this.restorable;
    }
}
