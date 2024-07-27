package com.gmail.prizmahdiep.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class FFAPlayer
{
    private final UUID player_uuid;
    private PlayerKit player_kit, last_player_kit;
    private SpawnLocation chosen_spawn, last_chosen_spawn;

    public FFAPlayer(Player p, PlayerKit pk, SpawnLocation spawn)
    {
        this.player_uuid = p.getUniqueId();
        this.player_kit = pk;
        this.last_player_kit = pk;
        this.chosen_spawn = spawn;
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(player_uuid);
    }

    public PlayerKit getPlayerKit()
    {
        return this.player_kit;
    }

    public PlayerKit getLastPlayerKit()
    {
        return this.last_player_kit;
    }
    
    public SpawnLocation getPlayerChosenSpawn()
    {
        return this.chosen_spawn;
    }

    public SpawnLocation getPlayerLastChosenSpawn()
    {
        return this.last_chosen_spawn;
    }

    public void setPlayerKit(PlayerKit pk)
    {
        this.last_player_kit = this.player_kit;
        if (pk == null)
        {
            PlayerKit empty = new PlayerKit("empty", new ItemStack[0], new ArrayList<>(0), false);
            this.player_kit = empty;
        }
        else this.player_kit = pk;
    }

    public void setPlayerSpawn(SpawnLocation s)
    {
        this.last_chosen_spawn = this.chosen_spawn;
        this.chosen_spawn = s;
    }

    public void restorePlayerKit()
    {

    }
}
