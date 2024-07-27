package com.gmail.prizmahdiep.commands;

import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.SpawnManager;

import co.aikar.commands.PaperCommandManager;

public class CommandHandler 
{
    PaperCommandManager manager;

    public CommandHandler(PaperCommandManager manager)
    {
        this.manager = manager;
    }

    public void registerCommands(SpawnManager sp, KitManager ku, FFAPlayersManager fph)
    {
        manager.registerCommand(new CommandFFAUtils(sp, ku, fph));
        manager.registerCommand(new CommandLoadme(fph));
        manager.registerCommand(new CommandUnloadme(fph, sp));
    }
}
