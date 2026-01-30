package com.tty.lib.services;

import org.bukkit.Location;

public interface FireworkService {

    /**
     * 在指定位置生成多枚烟花，延迟生成减少瞬间压力
     * @param center 爆炸中心
     * @param count  烟花数量
     */
    void spawnFireworks(Location center, int count);

    /**
     * 生成单枚烟花
     * @param center 爆炸中心
     */
    void spawnSingleFirework(Location center);
}
