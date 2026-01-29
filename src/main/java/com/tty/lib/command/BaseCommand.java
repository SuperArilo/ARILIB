package com.tty.lib.command;

import com.mojang.brigadier.context.CommandContext;
import com.tty.lib.annotations.CommandMeta;
import com.tty.lib.tool.LibConfigUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;


public abstract class BaseCommand {

    protected static final String[] PLUGIN_NAMES;

    static {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        Arrays.sort(plugins, (a, b) -> Integer.compare(b.getName().length(), a.getName().length()));
        PLUGIN_NAMES = Arrays.stream(plugins)
                .map(Plugin::getName)
                .toArray(String[]::new);
    }

    public abstract List<SuperHandsomeCommand> thenCommands();

    protected String getName() {
        CommandMeta meta = this.getClass().getAnnotation(CommandMeta.class);
        if (meta != null) return meta.displayName();
        throw new IllegalStateException(this.getClass().getSimpleName() + " lost @CommandMeta");
    }

    protected String getPermission() {
        CommandMeta meta = this.getClass().getAnnotation(CommandMeta.class);
        if (meta != null) return meta.permission();
        return "";
    }

    public abstract void execute(CommandSender sender, String[] args);

    protected abstract boolean isDisabledInGame(CommandSender sender, YamlConfiguration configuration);

    protected int preExecute(CommandContext<CommandSourceStack> ctx) {

        CommandMeta meta = this.getClass().getAnnotation(CommandMeta.class);
        CommandSender sender = ctx.getSource().getSender();

        if (!meta.allowConsole() && !(sender instanceof Player)) {
            sender.sendMessage(LibConfigUtils.t("function.public.not-player"));
            return 0;
        }

        if (!PermissionUtils.hasPermission(sender, this.getPermission())) {
            sender.sendMessage(LibConfigUtils.t("base.permission.no-permission"));
            return 0;
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

        if (args.length != meta.tokenLength()) {
            sender.sendMessage(LibConfigUtils.t("function.public.fail"));
            return 0;
        }

        this.execute(sender, args);

        return 1;
    }

}
