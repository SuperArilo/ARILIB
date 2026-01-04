package com.tty.lib.tool;

import com.tty.lib.Lib;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public record FireworkUtils(JavaPlugin plugin) {

    /**
     * 在指定位置生成多枚烟花，延迟生成减少瞬间压力
     *
     * @param center 爆炸中心
     * @param count  烟花数量
     */
    public void spawnFireworks(Location center, int count) {
        for (int i = 0; i < count; i++) {
            int delay = Math.max(1, i * 2);
            Lib.Scheduler.runAtRegionLater(this.plugin, center, t -> spawnSingleFirework(center), delay);
        }
    }

    /**
     * 生成单枚烟花
     *
     * @param center 爆炸中心
     */
    private void spawnSingleFirework(Location center) {

        double offsetX = (PublicFunctionUtils.randomGenerator(-150, 150) / 100.0); // -1.5 ~ 1.5
        double offsetY = PublicFunctionUtils.randomGenerator(100, 300) / 100.0;     // 1 ~ 3
        double offsetZ = (PublicFunctionUtils.randomGenerator(-150, 150) / 100.0); // -1.5 ~ 1.5
        Location loc = center.clone().add(offsetX, offsetY, offsetZ);

        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        int mainColorCount = PublicFunctionUtils.randomGenerator(1, 3);
        Color[] mainColors = new Color[mainColorCount];
        for (int j = 0; j < mainColorCount; j++) {
            mainColors[j] = Color.fromRGB(
                    PublicFunctionUtils.randomGenerator(0, 255),
                    PublicFunctionUtils.randomGenerator(0, 255),
                    PublicFunctionUtils.randomGenerator(0, 255)
            );
        }

        int fadeColorCount = PublicFunctionUtils.randomGenerator(1, 2);
        Color[] fadeColors = new Color[fadeColorCount];
        for (int j = 0; j < fadeColorCount; j++) {
            fadeColors[j] = Color.fromRGB(
                    PublicFunctionUtils.randomGenerator(0, 255),
                    PublicFunctionUtils.randomGenerator(0, 255),
                    PublicFunctionUtils.randomGenerator(0, 255)
            );
        }

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(mainColors)
                .withFade(fadeColors)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build();
        meta.addEffect(effect);
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.detonate();
    }

}
