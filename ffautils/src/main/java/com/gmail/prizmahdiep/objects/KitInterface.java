package com.gmail.prizmahdiep.objects;

import java.util.Collection;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public interface KitInterface 
{
    public boolean isRestorable();
    public ItemStack[] getInventory();
    public Collection<PotionEffect> getPotionEffects();
    public String getName();
}
