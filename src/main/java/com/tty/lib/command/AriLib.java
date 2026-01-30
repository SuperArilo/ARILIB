package com.tty.lib.command;

import com.tty.api.command.BaseLiteralArgumentLiteralCommand;
import com.tty.api.command.SuperHandsomeCommand;
import com.tty.api.annotations.CommandMeta;
import com.tty.api.annotations.LiteralCommand;
import com.tty.lib.command.sub.Reload;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandMeta(displayName = "arilib", allowConsole = true, tokenLength = 1)
@LiteralCommand
public class AriLib extends BaseLiteralArgumentLiteralCommand {

    @Override
    public List<SuperHandsomeCommand> thenCommands() {
        return List.of(new Reload());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

}
