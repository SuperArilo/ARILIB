package com.tty.lib.services;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface PermissionService {

    /**
     * 返回指定玩家的组id名称
     * @param player 被检查玩家
     * @return 返回的组id名称
     */
    String getPlayerGroup(Player player);

    /**
     *
     * @param player 被检查玩家
     * @param groupName 组名称
     * @return 返回该玩家是否存在于这个组
     */
    boolean getPlayerIsInGroup(Player player, String groupName);

    /**
     * 返回玩家是否具有对应的权限
     * @param player 被检查玩家
     * @param permission 权限字符串
     * @return 布尔值
     */
    boolean hasPermission(Player player, @NonNull String permission);

    /**
     * 返回玩家是否具有对应的权限
     * @param sender 被检查玩家
     * @param permission 权限字符串
     * @return 布尔值
     */
    boolean hasPermission(CommandSender sender, String permission);

    int getMaxCountInPermission(Player player, String typeString);

    boolean isNull();
}
