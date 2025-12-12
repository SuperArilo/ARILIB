package com.tty.lib.command;

import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;


public interface SuperHandsomeCommand {
    CommandNode<CommandSourceStack> toBrigadier();
    boolean isDisabledInGame(CommandSender sender, YamlConfiguration configuration);
}
