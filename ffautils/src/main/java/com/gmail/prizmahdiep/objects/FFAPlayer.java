package com.gmail.prizmahdiep.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FFAPlayer
{
    private final UUID player_uuid;
    private UUID opponent;
    private String player_kit, last_player_kit, chosen_spawn, last_chosen_spawn;

    public FFAPlayer(Player p, String kit, String spawn)
    {
        this.player_uuid = p.getUniqueId();
        this.last_player_kit = "none";
        this.player_kit = kit;
        this.last_chosen_spawn = "none";
        this.chosen_spawn = spawn;
        this.opponent = null;
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(player_uuid);
    }

    public UUID getOpponent()
    {
        return this.opponent;
    }

    public String getCurrentPlayerKitName()
    {
        return this.player_kit;
    }

    public String getLastPlayerKitName()
    {
        return this.last_player_kit;
    }
    
    public String getCurrentSpawnName()
    {
        return this.chosen_spawn;
    }

    public String getLastSpawnName()
    {
        return this.last_chosen_spawn;
    }

    public UUID getUUID()
    {
        return this.player_uuid;
    }

    public void setCurrentPlayerKitName(String pk)
    {
        this.player_kit = pk;
    }

    public void setCurrentSpawnName(String s)
    {
        this.chosen_spawn = s;
    }

    public void setLastPlayerKitName(String pk)
    {
        this.last_player_kit = pk;
    }

    public void setLastSpawnName(String s)
    {
        this.last_chosen_spawn = s;
    }

    public void setOpponent(UUID p)
    {
        this.opponent = p;
    }

    public boolean hasOpponent()
    {
        return this.opponent != null;
    }
}
