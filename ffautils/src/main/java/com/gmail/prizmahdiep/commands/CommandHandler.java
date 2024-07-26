package com.gmail.prizmahdiep.commands;

import com.gmail.prizmahdiep.handlers.FFAPlayersHandler;
import com.gmail.prizmahdiep.handlers.KitHandler;
import com.gmail.prizmahdiep.handlers.SpawnHandler;

import co.aikar.commands.PaperCommandManager;

public class CommandHandler 
{
    PaperCommandManager manager;

    public CommandHandler(PaperCommandManager manager)
    {
        this.manager = manager;
    }

    public void registerCommands(SpawnHandler sp, KitHandler ku, FFAPlayersHandler fph)
    {
        manager.registerCommand(new CommandFFAUtils(sp, ku, fph));
        manager.registerCommand(new CommandLoadme(fph));
        manager.registerCommand(new CommandUnloadme(fph, sp));
    }
}
