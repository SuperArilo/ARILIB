package com.tty.lib.services;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.function.Consumer;

public interface TeleportingService {

    TeleportingService aborted(Runnable runnable);
    TeleportingService before(Consumer<TeleportingService> consumer);
    void after(Runnable runnable);
    TeleportingService teleport(Entity entity, Location beforeLocation, Location targetLocation);
    void cancel();

}
