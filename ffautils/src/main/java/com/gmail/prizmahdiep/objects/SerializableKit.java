package com.gmail.prizmahdiep.objects;

import java.io.IOException;

import org.bukkit.inventory.ItemStack;

import com.gmail.prizmahdiep.utils.KitBase64Util;

public class SerializableKit 
{
    private String inventory_contents;
    private String armor;
    private String main_hand;
    private String off_hand;
    private String effects;
    private String display_name;
    private boolean restorable = false;
    private boolean editable = false;

    public SerializableKit(
        String serialized_inventory_contents, 
        String serialized_effects, 
        String armor,
        String offhand,
        String mainhand,
        boolean restorable, 
        boolean editable, 
        String thumnail_key, 
        String display_name
    )
    {
        this.inventory_contents = serialized_inventory_contents;
        this.armor = armor;
        this.off_hand = offhand;
        this.main_hand = mainhand;
        this.effects = serialized_effects;
        this.restorable = restorable;
        this.editable = editable;
        this.display_name = display_name;
    }

    public SerializableKit(Kit k)
    {
        this.inventory_contents = KitBase64Util.itemStackArrayToBase64(k.getInventory());
        this.armor = KitBase64Util.itemStackArrayToBase64(k.getArmorContents());
        this.main_hand = KitBase64Util.itemStackArrayToBase64(new ItemStack[]{k.getMainhandItem()});
        this.off_hand = KitBase64Util.itemStackArrayToBase64(new ItemStack[]{k.getOffhandItem()});
        this.effects = KitBase64Util.potionEffectCollectionToBase64(k.getPotionEffects());
        this.restorable = k.isRestorable();
        this.editable = k.isEditable();
        this.display_name = k.getDisplayName();
    }

    public Kit getKit(String name)
    {
        try 
        {
            return new Kit(
                        name, 
                        KitBase64Util.itemStackArrayFromBase64(this.inventory_contents), 
                        KitBase64Util.itemStackArrayFromBase64(this.armor),
                        KitBase64Util.itemStackArrayFromBase64(this.main_hand)[0],
                        KitBase64Util.itemStackArrayFromBase64(this.off_hand)[0],
                        KitBase64Util.potionEffectsFromBase64(this.effects), 
                        this.restorable, 
                        this.editable, 
                        display_name
            );
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return null;
    }
}
