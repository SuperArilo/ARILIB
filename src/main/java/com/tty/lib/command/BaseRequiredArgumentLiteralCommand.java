package com.tty.lib.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.tty.lib.Log;
import com.tty.lib.annotations.ArgumentCommand;
import com.tty.lib.tool.LibConfigUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Set;
import java.util.concurrent.CompletableFuture;


public abstract class BaseRequiredArgumentLiteralCommand<T> extends BaseCommand implements SuperHandsomeCommand {

    private final ArgumentType<T> type;

    public BaseRequiredArgumentLiteralCommand(ArgumentType<T> type) {
        this.type = type;
    }

    @Override
    public CommandNode<CommandSourceStack> toBrigadier() {
        RequiredArgumentBuilder<CommandSourceStack, T> builder = Commands.argument(this.getName(), this.type);
        builder.requires(ctx -> PermissionUtils.hasPermission(ctx.getSender(), this.getPermission()));
        builder.executes(this::preExecute);
        ArgumentCommand annotation = this.getClass().getAnnotation(ArgumentCommand.class);
        if (annotation != null && annotation.isSuggests()) {
            builder.suggests((ctx, b) -> {
                String input = ctx.getInput().trim().replaceFirst("/", "");
                for (String name : BaseCommand.PLUGIN_NAMES) {
                    if (input.startsWith(name + " ")) {
                        input = input.substring(name.length() + 1).trim();
                        break;
                    }
                }
                String[] args = input.isEmpty() ? new String[0] : input.split(" ");
                CompletableFuture<Set<String>> tabbed = this.tabSuggestions(ctx.getSource().getSender(), args);
                if (tabbed == null) return b.buildFuture();
                return tabbed.thenApply(list -> {
                    for (String s : list) {  b.suggest(s); }
                    return b.build();
                });
            });
        }
        for (SuperHandsomeCommand command : this.thenCommands()) {
            builder.then(command.toBrigadier());
        }

        return builder.build();
    }

    @Override
    protected boolean isDisabledInGame(CommandSender sender, YamlConfiguration configuration) {
        boolean b = configuration.getBoolean("main.enable", true);
        if (!b) {
            sender.sendMessage(LibConfigUtils.t("base.command.disabled"));
        }
        return b;
    }

    public abstract CompletableFuture<Set<String>> tabSuggestions(CommandSender sender, String[] args);

}
