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

import java.util.Set;
import java.util.concurrent.CompletableFuture;


public abstract class BaseRequiredArgumentLiteralCommand<T> extends BaseCommand implements SuperHandsomeCommand {

    private final ArgumentType<T> type;
    private final boolean isSuggests;

    /**
     * 基本构造函数
     * @param allowConsole 是否允许控制台补全执行
     * @param correctArgsLength 有效指令 args 携带参数长度。例如 /test ai (长度为 2
     * @param type 参数类型
     * @param isSuggests 是否允许进行自定义 Tab 补全列表
     */
    public BaseRequiredArgumentLiteralCommand(boolean allowConsole, Integer correctArgsLength, ArgumentType<T> type, boolean isSuggests) {
        super(allowConsole, correctArgsLength);
        this.type = type;
        this.isSuggests = isSuggests;
    }

    /**
     * 不允许控制台补全执行
     * @param correctArgsLength 有效指令 args 携带参数长度。例如 /test ai (长度为 2
     * @param type 参数类型
     * @param isSuggests 是否允许进行自定义 Tab 补全列表
     */
    public BaseRequiredArgumentLiteralCommand(int correctArgsLength, ArgumentType<T> type, boolean isSuggests) {
        super(false, correctArgsLength);
        this.type = type;
        this.isSuggests = isSuggests;
    }

    /**
     * 不允许控制台补全执行，不允许自定义 Tab 补全列表
     * @param correctArgsLength 有效指令 args 携带参数长度。例如 /test ai (长度为 2
     * @param type 参数类型
     */
    public BaseRequiredArgumentLiteralCommand(int correctArgsLength, ArgumentType<T> type) {
        super(false, correctArgsLength);
        this.isSuggests = false;
        this.type = type;
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
                return this.tabSuggestions(ctx.getSource().getSender(), args).thenApply(list -> {
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
    public boolean isDisabledInGame(CommandSender sender, YamlConfiguration configuration) {
        boolean b = configuration.getBoolean("main.enable", true);
        if (!b) {
            sender.sendMessage(LibConfigUtils.t("base.command.disabled"));
        }
        return b;
    }

    public abstract CompletableFuture<Set<String>> tabSuggestions(CommandSender sender, String[] args);

}
