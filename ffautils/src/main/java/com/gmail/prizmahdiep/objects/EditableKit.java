package com.gmail.prizmahdiep.objects;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class EditableKit
{
    private UUID owner;
    private ItemStack[] armor;
    private ItemStack[] storage;
    public static String A

    public EditableKit(String name, String editable_part, Collection<PotionEffect> effects, boolean restorable, UUID owner) 
    {
        
    }

    public UUID getOwner()
    {
        return owner;
    }
}
