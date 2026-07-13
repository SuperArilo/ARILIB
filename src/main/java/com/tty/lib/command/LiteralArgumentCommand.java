package com.tty.lib.command;

import com.tty.lib.Lib;
import com.tty.lib.tool.LibConfigUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import com.tty.api.command.BaseLiteralArgumentCommand;
import org.jetbrains.annotations.NotNull;


public abstract class LiteralArgumentCommand extends BaseLiteralArgumentCommand {

    protected LiteralArgumentCommand() {
        super(Lib.instance);
    }

    @Override
    protected boolean havePermission(CommandSender sender, String permission) {
        return Lib.PERMISSION_SERVICE.hasPermission(sender, permission);
    }

    @Override
    protected @NotNull Component tokenNotAllow() {
        return LibConfigUtils.t("function.public.fail");
    }

    @Override
    protected @NotNull Component onlyUseInGame() {
        return LibConfigUtils.t("function.public.not-player");
    }

    @Override
    protected boolean isEnableInGame() {
        return true;
    }

    @Override
    protected @NotNull Component disableInGame() {
        return LibConfigUtils.t("base.command.disabled");
    }

    @Override
    protected @NotNull Component taskAlreadyExits() {
        return LibConfigUtils.t("base.task.already-exits");
    }

}
