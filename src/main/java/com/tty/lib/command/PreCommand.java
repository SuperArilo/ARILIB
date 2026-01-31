package com.tty.lib.command;

import com.tty.api.command.AbstractSubCommand;
import com.tty.lib.Lib;
import com.tty.lib.tool.LibConfigUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class PreCommand extends AbstractSubCommand {

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
    protected boolean isDisabledInGame() {
        return false;
    }

    @Override
    protected @NotNull Component disableInGame() {
        return LibConfigUtils.t("base.command.disabled");
    }
}
