package com.gmail.prizmahdiep.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.objects.FFAPlayer;
import com.gmail.prizmahdiep.utils.PlayerUtils;

public class PlayerDisconnectListener implements Listener
{
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        if (FFAPlayersManager.ffa_players.get(p.getUniqueId()) != null)
            removeFromFFA(p);
        else
            PlayerUtils.resetPlayer(p);
    }

    private void removeFromFFA(Player p)
    {
        FFAPlayer fp = FFAPlayersManager.ffa_players.get(p.getUniqueId());
        if (fp != null) PlayerUtils.clearFFAPlayer(fp);
    }
}
