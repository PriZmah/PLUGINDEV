package com.gmail.prizmahdiep.objects;

import java.io.IOException;

import com.gmail.prizmahdiep.utils.KitBase64Util;

public class SerializableKit 
{
    private String inventory_contents;
    private String effects;
    private boolean restorable = false;
    private boolean editable = false;

    public SerializableKit(String serialized_inventory_contents, String serialized_effects, boolean restorable, boolean editable)
    {
        this.inventory_contents = serialized_inventory_contents;
        this.effects = serialized_effects;
        this.restorable = restorable;
        this.editable = editable;
    }

    public SerializableKit(Kit k)
    {
        this.inventory_contents = KitBase64Util.itemStackArrayToBase64(k.getInventory());
        this.effects = KitBase64Util.potionEffectCollectionToBase64(k.getPotionEffects());
        this.restorable = k.isRestorable();
        this.editable = k.isEditable();
    }

    public Kit getKit(String name)
    {
        try 
        {
            return new Kit(name, KitBase64Util.itemStackArrayFromBase64(this.inventory_contents), KitBase64Util.potionEffectsFromBase64(effects), this.restorable, this.editable);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return new Kit("empty", null, null, false, false);
        }
    }
}
