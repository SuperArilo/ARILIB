package com.tty.lib.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.tty.lib.tool.LibConfigUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;


public abstract class BaseRequiredArgumentLiteralCommand<T> extends BaseCommand implements SuperHandsomeCommand {

    private final ArgumentType<T> type;
    private final boolean isSuggests;

    public BaseRequiredArgumentLiteralCommand(boolean allowConsole, Integer correctArgsLength, ArgumentType<T> type, boolean isSuggests) {
        super(allowConsole, correctArgsLength);
        this.type = type;
        this.isSuggests = isSuggests;
    }

    @Override
    public CommandNode<CommandSourceStack> toBrigadier() {
        RequiredArgumentBuilder<CommandSourceStack, T> builder = Commands.argument(this.name(), this.type);
        builder.requires(ctx -> PermissionUtils.hasPermission(ctx.getSender(), this.permission()));
        builder.executes(this::preExecute);
        if (this.isSuggests) {
            builder.suggests((ctx, b) -> {
                String input = ctx.getInput().replace("ari ", "").trim();
                String[] args = input.isEmpty() ? new String[0] : input.split(" ");
                for (String s : this.tabSuggestions(ctx.getSource().getSender(), args)) {
                    b.suggest(s);
                }
                return b.buildFuture();
            });
        }
        for (SuperHandsomeCommand command : this.thenCommands()) {
            builder.then(command.toBrigadier());
        }

        return builder.build();
    }

    @Override
    public boolean isDisabledInGame(CommandSender sender, YamlConfiguration configuration) {
        boolean b = configuration.getBoolean("main.enable", true);
        if (!b) {
            sender.sendMessage(LibConfigUtils.t("base.command.disabled"));
        }
        return b;
    }

    public abstract List<String> tabSuggestions(CommandSender sender, String[] args);

}
