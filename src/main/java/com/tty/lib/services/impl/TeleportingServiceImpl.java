package com.tty.lib.services.impl;

import com.tty.api.ServerPlatform;
import com.tty.lib.Lib;
import com.tty.api.service.TeleportingService;
import com.tty.lib.tool.LibConfigUtils;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class TeleportingServiceImpl implements TeleportingService {

    protected boolean status = true;

    protected Consumer<TeleportingService> before;
    protected Runnable after = () -> {};
    protected Runnable aborted = () -> {};

    @Override
    public TeleportingService aborted(Runnable runnable) {
        this.aborted = runnable;
        return this;
    }

    @Override
    public TeleportingService before(Consumer<TeleportingService> consumer) {
        this.before = consumer;
        return this;
    }

    @Override
    public void after(Runnable runnable) {
        this.after = runnable;
    }

    @Override
    public TeleportingService teleport(Entity entity, Location beforeLocation, Location targetLocation) {
        if (this.before != null) {
            this.before.accept(this);
        }
        if (!this.status) {
            this.aborted.run();
            return this;
        }
        Lib.SCHEDULER.runAtRegion(Lib.instance, targetLocation, i -> {
            for (int y = 0;y <= targetLocation.getWorld().getMaxHeight();y++) {
                if (targetLocation.clone().add(0, y, 0).getBlock().isEmpty()) {
                    targetLocation.add(0, y, 0);
                    break;
                }
            }
            entity.teleportAsync(targetLocation,
                            PlayerTeleportEvent.TeleportCause.PLUGIN)
                    .thenAccept(p -> {
                        if (p) {
                            if (ServerPlatform.isFolia() && entity instanceof Player player) {
                                Bukkit.getPluginManager().callEvent(new PlayerTeleportEvent(player, beforeLocation, targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN));
                            }
                            entity.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f));
                        }
                        this.after.run();
                        entity.sendMessage(LibConfigUtils.t(p ? "function.teleport.success":"function.teleport.error"));
                    });
        });
        return this;
    }

    @Override
    public void cancel() {
        this.status = false;
    }

}
