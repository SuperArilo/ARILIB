package com.tty.lib.commands.sub;

import com.tty.api.annotations.command.CommandMeta;
import com.tty.api.annotations.command.LiteralCommand;
import com.tty.api.command.SuperHandsomeCommand;
import com.tty.api.event.CustomPluginReloadEvent;
import com.tty.lib.command.LiteralArgumentCommand;
import com.tty.lib.tool.LibConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandMeta(displayName = "reload", permission = "arilib.command.reload", tokenLength = 1, allowConsole = true)
@LiteralCommand(directExecute = true)
public class Reload extends LiteralArgumentCommand {

    @Override
    public List<SuperHandsomeCommand> thenCommands() {
        return List.of();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(LibConfigUtils.t("function.reload.doing"));
        Bukkit.getPluginManager().callEvent(new CustomPluginReloadEvent(sender));
    }
}
