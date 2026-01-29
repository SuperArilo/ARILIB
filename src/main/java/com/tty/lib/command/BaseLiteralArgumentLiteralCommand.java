package com.tty.lib.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.tty.lib.annotations.LiteralCommand;
import com.tty.lib.tool.LibConfigUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("SameReturnValue")
public abstract class BaseLiteralArgumentLiteralCommand extends BaseCommand implements SuperHandsomeCommand {

    @Override
    protected boolean isDisabledInGame(CommandSender sender, @NonNull YamlConfiguration configuration) {
        boolean b = configuration.getBoolean("main.enable", true);
        if (!b) {
            sender.sendMessage(LibConfigUtils.t("base.command.disabled"));
        }
        return b;
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> toBrigadier() {
        LiteralArgumentBuilder<CommandSourceStack> top_mian = Commands.literal(this.getName());
        top_mian.requires(ctx -> PermissionUtils.hasPermission(ctx.getSender(), this.getPermission()));
        LiteralCommand annotation = this.getClass().getAnnotation(LiteralCommand.class);
        if (annotation != null && annotation.directExecute()) {
            top_mian.executes(this::preExecute);
        }
        for (SuperHandsomeCommand subCommand : this.thenCommands()) {
            top_mian.then(subCommand.toBrigadier());
        }
        return top_mian.build();
    }

}
