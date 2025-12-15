package com.tty.lib.enum_type;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public enum Operator {

    CONSOLE("00000000-0000-0000-0000-000000000000");

    @Getter
    private final String uuid;

    Operator(String uuid) {
        this.uuid = uuid;
    }

    public static UUID getOperator(CommandSender sender) {
        if (sender instanceof Player p) {
            return p.getUniqueId();
        }
        return UUID.fromString(CONSOLE.getUuid());
    }
}
