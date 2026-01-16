package com.tty.lib.services.placeholder;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface AsyncPlaceholder {

    CompletableFuture<Component> resolve(OfflinePlayer context);

}
