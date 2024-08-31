package com.gmail.prizmahdiep.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class TestListener implements Listener
{
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e)
    {
        e.getWhoClicked().sendMessage(e.getCursor().getType().toString());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        e.getWhoClicked().sendMessage(e.getSlot() + "");
    }
}
