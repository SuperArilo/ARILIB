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
public abstract class BaseLiteralArgumentLiteralCommand extends BaseLiteralCommand implements SuperHandsomeCommand {

    @Override
    public boolean isDisabledInGame(CommandSender sender, @NonNull YamlConfiguration configuration) {
        boolean b = configuration.getBoolean("main.enable", true);
        if (!b) {
            sender.sendMessage(LibConfigUtils.t("base.command.disabled"));
        }
        return b;
    }

    protected BaseLiteralArgumentLiteralCommand(boolean allowConsole, int correctArgsLength) {
        super(allowConsole, correctArgsLength);
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> toBrigadier() {
        LiteralArgumentBuilder<CommandSourceStack> top_mian = Commands.literal(this.name());
        top_mian.requires(ctx -> PermissionUtils.hasPermission(ctx.getSender(), this.permission()));
        if (this.thenCommands().isEmpty()) {
            top_mian.executes(this::baseExecute);
        }
        for (SuperHandsomeCommand subCommand : this.thenCommands()) {
            top_mian.then(subCommand.toBrigadier());
        }
        return top_mian.build();
    }

}
