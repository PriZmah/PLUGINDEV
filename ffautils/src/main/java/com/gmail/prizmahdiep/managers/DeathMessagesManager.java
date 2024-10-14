package com.gmail.prizmahdiep.managers;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.gmail.prizmahdiep.config.DeathMessagesConfig;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class DeathMessagesManager 
{
    private DeathMessagesConfig dmconf;
    private MiniMessage mm;
    private List<String> death_messages;

    public DeathMessagesManager(DeathMessagesConfig death_messages_config, MiniMessage mm) 
    {
        this.dmconf = death_messages_config;
        this.mm = mm;
        this.death_messages = dmconf.getConfig().getStringList("messages");
    }

    public List<String> getDeathMessages() 
    {
        return this.death_messages;
    }

    public TextComponent parseDeathMessage(Player victim, Player killer, double current_health, String death_message) 
    {
        
        Map<String, String> component_variables = Map.of
        (
            "{victim_username}", ((TextComponent)victim.name()).content().toString(),
            "{attacker_username}", ((TextComponent)killer.name()).content().toString(),
            "{attacker_hp}", String.format("%.2f", current_health),
            "{attacker_hearts}", String.format("%.1f", current_health / 2)
        );

        for (Map.Entry<String, String> entry : component_variables.entrySet()) 
        {
            String placeholder = entry.getKey();
            String component = entry.getValue();
            death_message = death_message.replace(placeholder, component);
        }

        return (TextComponent) mm.deserialize(death_message);
    }
    
    public void reload()
    {
        dmconf.reload();
        this.death_messages = dmconf.getConfig().getStringList("messages");
    }
}
