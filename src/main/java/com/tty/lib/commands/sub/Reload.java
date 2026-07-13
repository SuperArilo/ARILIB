package com.tty.lib.commands.sub;

import com.tty.api.annotations.command.CommandMeta;
import com.tty.api.annotations.command.LiteralCommand;
import com.tty.api.command.SuperHandsomeCommand;
import com.tty.api.event.WhenPluginExecuteReloadCommandEvent;
import com.tty.lib.Lib;
import com.tty.lib.command.LiteralArgumentCommand;
import com.tty.lib.tool.LibConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@CommandMeta(displayName = "reload", permission = "arilib.command.reload", tokenLength = 1, allowConsole = true)
@LiteralCommand(directExecute = true)
public class Reload extends LiteralArgumentCommand {

    @Override
    public List<SuperHandsomeCommand> thenCommands() {
        return List.of();
    }

    @Override
    public CompletableFuture<Void> execute(CommandSender sender, String[] args) {
        sender.sendMessage(LibConfigUtils.t("function.reload.doing"));
        Lib.instance.getScheduler().run(i -> Bukkit.getPluginManager().callEvent(new WhenPluginExecuteReloadCommandEvent(Lib.instance, sender)));
        return CompletableFuture.completedFuture(null);
    }
}
