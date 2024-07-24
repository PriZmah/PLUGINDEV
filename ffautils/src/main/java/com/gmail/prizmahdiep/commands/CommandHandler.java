package com.gmail.prizmahdiep.commands;

import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.utils.KitUtils;
import com.gmail.prizmahdiep.utils.SpawnUtils;

import co.aikar.commands.PaperCommandManager;

public class CommandHandler 
{
    PaperCommandManager manager;

    public CommandHandler(PaperCommandManager manager)
    {
        this.manager = manager;
    }

    public void registerCommands(SpawnUtils sp, KitUtils ku, FFAPlayersHandler fph, SpawnUtils sputils)
    {
        manager.registerCommand(new CommandFFAUtils(sp, ku, fph));
        manager.registerCommand(new CommandLoadme(fph));
        manager.registerCommand(new CommandUnloadme(fph, sputils));
    }
}
