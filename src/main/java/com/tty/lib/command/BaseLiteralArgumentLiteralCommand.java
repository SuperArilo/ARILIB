package com.tty.lib.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.tty.lib.tool.LibConfigUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("SameReturnValue")
public abstract class BaseLiteralArgumentLiteralCommand extends BaseCommand implements SuperHandsomeCommand {

    /**
     * 是否直接执行
     */
    private boolean direct_execute = false;

    /**
     * 不允许控制台执行, 也不允许直接执行
     * @param correctArgsLength 有效指令 args 携带参数长度。例如 /test ai (长度为 2
     */
    public BaseLiteralArgumentLiteralCommand(int correctArgsLength) {
        super(false, correctArgsLength);
    }

    /**
     * 默认狗仔函数，不允许直接执行
     * @param allowConsole 是否允许控制台执行
     * @param correctArgsLength 有效指令 args 携带参数长度。例如 /test ai (长度为 2
     */
    public BaseLiteralArgumentLiteralCommand(boolean allowConsole, Integer correctArgsLength) {
        super(allowConsole, correctArgsLength);
    }

    /**
     * 默认狗仔函数
     * @param allowConsole 是否允许控制台执行
     * @param correctArgsLength 有效指令 args 携带参数长度。例如 /test ai (长度为 2
     * @param direct_execute 是否允许直接执行
     */
    public BaseLiteralArgumentLiteralCommand(boolean allowConsole, Integer correctArgsLength, boolean direct_execute) {
        super(allowConsole, correctArgsLength);
        this.direct_execute = direct_execute;
    }

    @Override
    public boolean isDisabledInGame(CommandSender sender, @NonNull YamlConfiguration configuration) {
        boolean b = configuration.getBoolean("main.enable", true);
        if (!b) {
            sender.sendMessage(LibConfigUtils.t("base.command.disabled"));
        }
        return b;
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> toBrigadier() {
        LiteralArgumentBuilder<CommandSourceStack> top_mian = Commands.literal(this.name());
        top_mian.requires(ctx -> PermissionUtils.hasPermission(ctx.getSender(), this.permission()));
        if (this.direct_execute) {
            top_mian.executes(this::preExecute);
        }
        for (SuperHandsomeCommand subCommand : this.thenCommands()) {
            top_mian.then(subCommand.toBrigadier());
        }
        return top_mian.build();
    }

}
