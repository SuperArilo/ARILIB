package com.tty.lib.services.placeholder;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@FunctionalInterface
public interface PlaceholderResolve {

    CompletableFuture<Component> resolve(OfflinePlayer context);

    static PlaceholderResolve of(Function<Player, CompletableFuture<Component>> playerFunc, Function<OfflinePlayer, CompletableFuture<Component>> offlineFunc) {
        return context -> {
            if (context == null) {
                throw new IllegalArgumentException("context not allowed null");
            }
            if (context instanceof Player player) {
                return playerFunc.apply(player);
            } else {
                return offlineFunc.apply(context);
            }
        };
    }

    static PlaceholderResolve ofPlayer(Function<Player, CompletableFuture<Component>> function) {
        return of(function, offlinePlayer -> CompletableFuture.completedFuture(Component.empty()));
    }

    static PlaceholderResolve ofOfflinePlayer(Function<OfflinePlayer, CompletableFuture<Component>> function) {
        return function::apply;
    }

}
