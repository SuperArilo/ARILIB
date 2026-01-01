package com.tty.lib.command;

import com.tty.lib.command.sub.Reload;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AriLib extends BaseLiteralArgumentLiteralCommand {

    public AriLib() {
        super(true, 1, true);
    }

    @Override
    public List<SuperHandsomeCommand> thenCommands() {
        return List.of(new Reload());
    }

    @Override
    public String name() {
        return "arilib";
    }

    @Override
    public String permission() {
        return "";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
