package com.gmail.prizmahdiep.managers;

import java.util.Set;

import com.gmail.prizmahdiep.config.BlockedCommandsConfig;

public class BlockedCommandsManager 
{
    private BlockedCommandsConfig bcf;
    private Set<String> blocked_commands;
    
    public BlockedCommandsManager(BlockedCommandsConfig bcf)
    {
        this.bcf = bcf;
        this.blocked_commands = Set.copyOf(this.bcf.getConfig().getStringList("blocked-commands"));
        
    }

    public Set<String> getBlockedCommands()
    {
        return this.blocked_commands;
    }

    public void reload()
    {
        bcf.reload();
        this.blocked_commands = Set.copyOf(this.bcf.getConfig().getStringList("blocked-commands"));;
    }
}
