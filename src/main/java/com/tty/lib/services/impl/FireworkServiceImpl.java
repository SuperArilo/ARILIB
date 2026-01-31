package com.tty.lib.services.impl;

import com.tty.api.PublicFunctionUtils;
import com.tty.lib.Lib;
import com.tty.api.service.FireworkService;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public record FireworkServiceImpl() implements FireworkService {

    @Override
    public void spawnFireworks(Location center, int count) {
        for (int i = 0; i < count; i++) {
            int delay = Math.max(1, i * 2);
            Lib.SCHEDULER.runAtRegionLater(Lib.instance, center, t -> spawnSingleFirework(center), delay);
        }
    }

    @Override
    public void spawnSingleFirework(Location center) {
        double offsetX = (PublicFunctionUtils.randomGenerator(-150, 150) / 100.0);
        double offsetY = PublicFunctionUtils.randomGenerator(100, 300) / 100.0;
        double offsetZ = (PublicFunctionUtils.randomGenerator(-150, 150) / 100.0);
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
