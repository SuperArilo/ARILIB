package com.tty.lib.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.tty.lib.Log;
import com.tty.lib.dto.AliasItem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class CommandRegister {

    public static void register(JavaPlugin plugin, String packagePath, Map<String, AliasItem> aliasItemMap) {
        Log.debug("----------register commands ----------");
        long start = System.currentTimeMillis();
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();
            aliasItemMap.forEach((k, v) -> {
                if(!v.isEnable()) return;
                Class<?> executorClass;
                try {
                    executorClass = Class.forName(packagePath + "." + k);
                } catch (ClassNotFoundException e) {
                    Log.error(e, "Error while constructing instruction. {} class not found!", k);
                    return;
                }
                Object executorInstance;
                try {
                    executorInstance = executorClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    Log.error(e, "Error while constructing executor for instruction: {}", k);
                    return;
                }
                if (executorInstance instanceof SuperHandsomeCommand cmd) {
                    commands.register((LiteralCommandNode<CommandSourceStack>) cmd.toBrigadier(), v.getUsage());
                    Log.debug((v.isEnable() ? "":"un" ) + "register command: {}", k);
                }
            });
            Log.debug("register commands time: {}ms", (System.currentTimeMillis() - start));
        });
    }

}
