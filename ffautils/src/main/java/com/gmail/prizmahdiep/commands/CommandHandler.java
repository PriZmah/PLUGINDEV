package com.gmail.prizmahdiep.commands;

import com.gmail.prizmahdiep.FFAUtils;
import com.gmail.prizmahdiep.managers.BlockedCommandsManager;
import com.gmail.prizmahdiep.managers.DeathMessagesManager;
import com.gmail.prizmahdiep.managers.FFAPlayersManager;
import com.gmail.prizmahdiep.managers.KitEditorManager;
import com.gmail.prizmahdiep.managers.KitManager;
import com.gmail.prizmahdiep.managers.RespawnItemManager;
import com.gmail.prizmahdiep.managers.SelectorManager;
import com.gmail.prizmahdiep.managers.SpawnManager;

import co.aikar.commands.PaperCommandManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class CommandHandler 
{
    private PaperCommandManager manager;
    private FFAUtils pl;

    public CommandHandler(PaperCommandManager manager, FFAUtils pl)
    {
        this.manager = manager;
        this.pl = pl;
    }

    public void registerCommands(SpawnManager sm, KitManager km, FFAPlayersManager fph, KitEditorManager kem, SelectorManager sem, MiniMessage minimessage_deserializer, RespawnItemManager rim, DeathMessagesManager dmm, BlockedCommandsManager bcm)
    {
        manager.registerCommand(new CommandFFAUtils(sm, km, fph, pl, kem, sem, minimessage_deserializer, rim, dmm, bcm));
        manager.registerCommand(new CommandLoadme(fph, km, sm, kem));
        manager.registerCommand(new CommandUnloadme(fph, sm));
        manager.registerCommand(new CommandEditKit(km, kem, pl, fph, sm));
    }
}
