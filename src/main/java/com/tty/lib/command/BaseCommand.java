package com.tty.lib.command;

import com.tty.lib.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseCommand {

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
        } catch (Exception e) {
            Log.debug(e, "not a uuid: %s", value);
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

}
