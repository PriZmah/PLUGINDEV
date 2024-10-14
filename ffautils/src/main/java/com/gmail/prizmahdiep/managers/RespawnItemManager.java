package com.gmail.prizmahdiep.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.gmail.prizmahdiep.FFAUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class RespawnItemManager 
{
    private ItemStack respawn_item;
    private String item_type;
    private String item_display_name;
    private String item_lore;
    private MiniMessage mm;
    private FFAUtils futils;

    public RespawnItemManager(MiniMessage mim, FFAUtils ffutils)
    {
        this.mm = mim;
        this.futils = ffutils;
        reload();
    }

    public ItemStack getFastRespawnItem()
    {
        return new ItemStack(respawn_item);
    }

    public void reload()
    {
        FileConfiguration config = futils.getConfig();
        item_type = config.getString("fast-respawn-item-type");
        item_display_name = config.getString("fast-respawn-item-display-name");
        item_lore = config.getString("fast-respawn-item-lore");

        this.respawn_item = new ItemStack(
            Material.getMaterial(item_type)
        );

        NamespacedKey key = new NamespacedKey(futils, "respawn-item-type");
        ItemMeta respawn_item_meta = respawn_item.getItemMeta();
        respawn_item_meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        respawn_item_meta.displayName(mm.deserialize("<!i>" + item_display_name));

        List<Component> lores = new ArrayList<>();
        lores.add(mm.deserialize("<!i>" + item_lore));

        respawn_item_meta.lore(lores);

        respawn_item.setItemMeta(respawn_item_meta);
    }
}
