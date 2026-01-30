package com.tty.lib.services;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public interface EconomyService {

    /**
     * 为指定玩家增长指定的金钱
     * @param player 玩家
     * @param cost 增加金钱数量
     * @return 返沪 EconomyResponse
     */
    EconomyResponse depositPlayer(Player player, double cost);

    /**
     * 为指定玩家扣除相应的金钱
     * @param player 指定玩家
     * @param cost 扣除的金钱数量
     * @return 返沪 EconomyResponse
     */
    EconomyResponse withdrawPlayer(Player player, double cost);

    /**
     * 获取指定玩家的存款数量
     * @param player 指定玩家
     * @return 返回该玩家的存款
     */
    Double getBalance(Player player);

    /**
     * 检查指定玩家是否有足够的金钱
     * @param player 玩家
     * @param cost 花费的金额
     * @return true 足够，false 不足够
     */
    boolean hasEnoughBalance(Player player, double cost);

    /**
     * 获取复数形式的货币名称
     * @return 复数形式
     */
    String getNamePlural();

    /**
     * 获取单数形式的货币名称
     * @return 单数形式
     */
    String getNameSingular();

    boolean isNull();

}
