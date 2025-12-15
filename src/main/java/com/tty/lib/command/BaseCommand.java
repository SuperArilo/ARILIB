package com.tty.lib.command;

import com.mojang.brigadier.context.CommandContext;
import com.tty.lib.tool.LibConfigUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public abstract class BaseCommand {

    private final boolean allowConsole;
    private final Integer correctArgsLength;

    protected BaseCommand(boolean allowConsole, Integer correctArgsLength) {
        this.allowConsole = allowConsole;
        this.correctArgsLength = correctArgsLength;
    }

    public abstract List<SuperHandsomeCommand> thenCommands();

    public abstract String name();

    public abstract String permission();

    public abstract void execute(CommandSender sender, String[] args);

    /**
     * 根据输入参数解析 UUID
     * @param value 玩家名字或 UUID
     * @return 玩家 UUID，如果不存在则返回 null
     */
    protected UUID parseUUID(String value) {
        AtomicReference<UUID> uuid = new AtomicReference<>(null);
        try {
            uuid.set(UUID.fromString(value));
        } catch (Exception ignored) {
        }
        if (uuid.get() == null) {
            try {
                uuid.set(Bukkit.getOfflinePlayer(value).getUniqueId());
            } catch (Exception e) {
                return null;
            }
        }
        return uuid.get();
    }

    int preExecute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!this.allowConsole && !(sender instanceof Player)) {
            sender.sendMessage(LibConfigUtils.t("function.public.not-player"));
            return SINGLE_SUCCESS;
        }
        if(!PermissionUtils.hasPermission(sender, this.permission())) {
            sender.sendMessage(LibConfigUtils.t("base.permission.no-permission"));
            return SINGLE_SUCCESS;
        }
        String input = ctx.getInput().replace("ari ", "").trim();
        String[] args = input.isEmpty() ? new String[0] : input.split(" ");
        if (args.length != this.correctArgsLength) {
            sender.sendMessage(LibConfigUtils.t("function.public.fail"));
            return SINGLE_SUCCESS;
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("\"") && arg.endsWith("\"") && arg.length() >= 2) {
                args[i] = arg.substring(1, arg.length() - 1);
            }
        }
        this.execute(sender, args);

        return SINGLE_SUCCESS;
    }

}
