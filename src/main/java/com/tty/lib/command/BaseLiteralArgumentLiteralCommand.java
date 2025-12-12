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

    private boolean twiceDo = false;

    public BaseLiteralArgumentLiteralCommand(boolean allowConsole) {
        super(allowConsole);
    }


    public BaseLiteralArgumentLiteralCommand(boolean allowConsole, Integer correctArgsLength) {
        super(allowConsole, correctArgsLength);
    }

    public BaseLiteralArgumentLiteralCommand(boolean allowConsole, Integer correctArgsLength, boolean twiceDo) {
        super(allowConsole, correctArgsLength);
        this.twiceDo = twiceDo;
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
        if (this.twiceDo) {
            top_mian.executes(this::preExecute);
        }
        for (SuperHandsomeCommand subCommand : this.thenCommands()) {
            top_mian.then(subCommand.toBrigadier());
        }
        return top_mian.build();
    }

}
