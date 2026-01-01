package com.tty.lib.command.sub;

import com.tty.lib.command.BaseLiteralArgumentLiteralCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.dto.event.CustomPluginReloadEvent;
import com.tty.lib.tool.LibConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends BaseLiteralArgumentLiteralCommand {


    public Reload() {
        super(true, 1, true);
    }

    @Override
    public List<SuperHandsomeCommand> thenCommands() {
        return List.of();
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "arilib.command.reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(LibConfigUtils.t("function.reload.doing"));
        Bukkit.getPluginManager().callEvent(new CustomPluginReloadEvent(sender));
    }
}
