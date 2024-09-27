package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.events.KitEditorPlayerLoadEvent;
import com.gmail.prizmahdiep.events.KitEditorPlayerUnloadEvent;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.SpawnLocation;
import com.gmail.prizmahdiep.utils.PlayerUtils;

public class KitEditorListener implements Listener
{
    private SpawnManager sm;
    private FFAPlayersManager fph;
    private FFAUtils pl;
    private KitEditorManager kem;

    public KitEditorListener(SpawnManager sm, FFAPlayersManager fph, FFAUtils pl, KitEditorManager kem)
    {
        this.sm = sm;
        this.fph = fph;
        this.pl = pl;
        this.kem = kem;
    }

    @EventHandler
    public void onKitEditorStart(KitEditorPlayerLoadEvent ev)
    {
        final Kit k = ev.getEditableKit();
        Player p = ev.getPlayer();
        fph.removePlayerFromIdle(p.getUniqueId());
        PlayerUtils.teleportPlayerToSpawn(p, sm.getSpawnOfType(SpawnLocation.EDITOR_ROOM));
        PlayerUtils.setPlayerKit(p, k);

        new BukkitRunnable() {
            @Override
            public void run()
            {
                if (kem.EditedKitExists(p.getUniqueId(), k.getName()))
                    new BukkitRunnable() {
                        @Override
                        public void run()
                        {
                            p.getInventory().setContents(kem.getEditedKit(p.getUniqueId(), k.getName()));
                        }
                    }.runTask(pl);
            }
        }.runTaskAsynchronously(pl);
    } 
    
    @EventHandler
    public void onKitEditorEnd(KitEditorPlayerUnloadEvent ev)
    {
        PlayerUtils.resetPlayer(ev.getPlayer());
    }
}
