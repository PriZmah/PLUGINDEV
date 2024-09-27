package com.gmail.prizmahdiep.objects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class Kit
{ 
    private String name;
    private String display_name;
    private ItemStack[] inventory_contents;
    private ItemStack[] armor_contents;
    private ItemStack main_hand;
    private ItemStack off_hand;
    private boolean restorable = false;
    private boolean editable = false;
    private Collection<PotionEffect> effects;

    public Kit(
            String name, 
            ItemStack[] inventory_contents, 
            ItemStack[] armor_contents,
            ItemStack main_hand,
            ItemStack offhand,
            Collection<PotionEffect> effects, 
            boolean restorable, 
            boolean editable, 
            String display_name
    )
    {
        this.name = name;
        this.inventory_contents = inventory_contents;
        this.armor_contents = armor_contents;
        this.main_hand = main_hand;
        this.off_hand = offhand;
        this.effects = effects;
        this.restorable = restorable;
        this.editable = editable;
        this.display_name = display_name;
    }

    public String getName()
    {
        return this.name;
    }

    public ItemStack[] getInventory()
    {
        return this.inventory_contents;
    }

    public ItemStack[] getArmorContents()
    {
        return this.armor_contents;
    }

    public ItemStack getMainhandItem()
    {
        return this.main_hand;
    }

    public ItemStack getOffhandItem()
    {
        return this.off_hand;
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

    public String getDisplayName()
    {
        return this.display_name;
    }
}
