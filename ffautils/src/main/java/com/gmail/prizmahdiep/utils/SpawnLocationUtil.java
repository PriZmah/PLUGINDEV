package com.gmail.prizmahdiep.utils;

import com.gmail.prizmahdiep.managers.SpawnManager;
import com.gmail.prizmahdiep.objects.SpawnLocation;

public class SpawnLocationUtil 
{
    public static boolean isValidSpawn(SpawnLocation spawn)
    {
        if (spawn.getLocation().getWorld() == null) return false;
        String spawntype = spawn.getType();
        if (!spawntype.equals(SpawnLocation.EDITOR_ROOM) && !spawntype.equals(SpawnLocation.FTN)
        && !spawntype.equals(SpawnLocation.SPAWN) && !spawntype.equals(SpawnLocation.STANDARD)) return false;

        for (SpawnLocation i : SpawnManager.spawns.values())
            if (spawn.getType().equals(i.getType()) && i.getType().equals(SpawnLocation.SPAWN)
                && !spawn.equals(i)) return false;
        return true;
    }
}
