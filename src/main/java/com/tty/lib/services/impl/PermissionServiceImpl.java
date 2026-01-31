package com.tty.lib.services.impl;

import com.tty.lib.Lib;
import com.tty.api.service.PermissionService;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

public record PermissionServiceImpl(Permission PERMISSION) implements PermissionService {

    @Override
    public String getPlayerGroup(Player player) {
        return isNull() ? "default":PERMISSION.getPrimaryGroup(player);
    }

    @Override
    public boolean getPlayerIsInGroup(Player player, String groupName) {
        return isNull() || PERMISSION.playerInGroup(player, groupName);
    }

    @Override
    public boolean hasPermission(Player player, @NonNull String permission) {
        return isNull() ? player.hasPermission(permission):PERMISSION.has(player, permission);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permission) {
        if (permission.isEmpty()) return true;
        return isNull() ? sender.hasPermission(permission):PERMISSION.has(sender, permission);
    }

    @Override
    public int getMaxCountInPermission(Player player, String typeString) {
        if (player.isOp()) return Integer.MAX_VALUE;
        int initValue = 0;
        String firstErrorPermission = null;
        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            String permission = permissionInfo.getPermission();
            if (!permission.startsWith("ari.count." + typeString + ".")) continue;
            String[] parts = permission.split("\\.");
            if (parts.length < 4) {
                if (firstErrorPermission == null) firstErrorPermission = permission;
                continue;
            }
            try {
                int count = Integer.parseInt(parts[3]);
                if (count > initValue) initValue = count;
            } catch (NumberFormatException e) {
                if (firstErrorPermission == null) firstErrorPermission = permission;
            }
        }
        if (initValue == 0 && firstErrorPermission != null) {
            Lib.log.error("player {} permission format error: ", player.getName(), firstErrorPermission);
        }
        return initValue;
    }

    @Override
    public boolean isNull() {
        return PERMISSION == null;
    }
}
