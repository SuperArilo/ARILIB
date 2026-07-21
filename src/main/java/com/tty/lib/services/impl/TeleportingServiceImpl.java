package com.tty.lib.services.impl;

import com.tty.api.scheduler.Scheduler;
import com.tty.api.service.TeleportingService;
import com.tty.lib.Lib;
import com.tty.lib.tool.LibConfigUtils;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.function.Consumer;

import static org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH;

public class TeleportingServiceImpl implements TeleportingService {

    protected volatile boolean status = true;

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
        if (!this.status || !(entity instanceof Player player)) {
            this.aborted.run();
            return this;
        }

        if (this.before != null) {
            this.before.accept(this);
        }

        Lib.instance.getScheduler().runAtRegion(targetLocation, i -> {
            for (int y = 0;y <= targetLocation.getWorld().getMaxHeight();y++) {
                if (targetLocation.clone().add(0, y, 0).getBlock().isEmpty()) {
                    targetLocation.add(0, y, 0);
                    break;
                }
            }

            boolean isFolia = Scheduler.isFolia();
            boolean cancelled = false;

            if (isFolia) {
                PlayerTeleportEvent event = new PlayerTeleportEvent(player, beforeLocation, targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                cancelled = !event.callEvent();
            }

            if (cancelled) {
                this.after.run();
                entity.sendMessage(LibConfigUtils.t("function.teleport.error"));
                return;
            }

            entity.teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(status -> {
                if (status) {
                    entity.playSound(Sound.sound(ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f));
                }
                this.after.run();
                entity.sendMessage(LibConfigUtils.t(status ? "function.teleport.success":"function.teleport.error"));
            });
        });
        return this;
    }

    @Override
    public void cancel() {
        this.status = false;
    }

}
