package com.tty.lib.command;

import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;


public interface SuperHandsomeCommand {
    CommandNode<CommandSourceStack> toBrigadier();
}
