package com.tty.lib.command;

import com.mojang.brigadier.context.CommandContext;
import com.tty.lib.tool.LibConfigUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public abstract class BaseCommand {

    private final boolean allowConsole;
    private final Integer correctArgsLength;

    protected static final String[] PLUGIN_NAMES;

    static {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        Arrays.sort(plugins, (a, b) -> Integer.compare(b.getName().length(), a.getName().length()));
        PLUGIN_NAMES = Arrays.stream(plugins)
                .map(Plugin::getName)
                .toArray(String[]::new);
    }

    protected BaseCommand(boolean allowConsole, Integer correctArgsLength) {
        this.allowConsole = allowConsole;
        this.correctArgsLength = correctArgsLength;
    }

    public abstract List<SuperHandsomeCommand> thenCommands();

    public abstract String name();

    public abstract String permission();

    public abstract void execute(CommandSender sender, String[] args);

    int preExecute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!this.allowConsole && !(sender instanceof Player)) {
            sender.sendMessage(LibConfigUtils.t("function.public.not-player"));
            return SINGLE_SUCCESS;
        }

        if (!PermissionUtils.hasPermission(sender, this.permission())) {
            sender.sendMessage(LibConfigUtils.t("base.permission.no-permission"));
            return SINGLE_SUCCESS;
        }

        String input = ctx.getInput().trim();

        for (String name : PLUGIN_NAMES) {
            if (input.startsWith(name + " ")) {
                input = input.substring(name.length()).trim();
                break;
            }
        }

        String[] args = input.isEmpty() ? new String[0] : input.split(" ");

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("\"") && arg.endsWith("\"") && arg.length() >= 2) {
                args[i] = arg.substring(1, arg.length() - 1);
            }
        }

        if (this.correctArgsLength != null && args.length != this.correctArgsLength) {
            sender.sendMessage(LibConfigUtils.t("function.public.fail"));
            return SINGLE_SUCCESS;
        }

        this.execute(sender, args);

        return SINGLE_SUCCESS;
    }

}
