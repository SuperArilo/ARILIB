package com.tty.lib.services.placeholder;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@FunctionalInterface
public interface AsyncPlaceholder extends Placeholder {

    CompletableFuture<Component> resolve(OfflinePlayer context);

    /**
     * 同时提供 Player 和 OfflinePlayer 的占位符构建器
     *
     * @param playerFunc   Player
     * @param offlineFunc  OfflinePlayer
     * @return AsyncPlaceholder
     */
    static AsyncPlaceholder of(Function<Player, CompletableFuture<Component>> playerFunc, Function<OfflinePlayer, CompletableFuture<Component>> offlineFunc) {
        return context -> {
            if (context instanceof Player player) {
                CompletableFuture<Component> fut = playerFunc.apply(player);
                return fut.thenApply(c -> c == null ? Component.empty() : c);
            } else {
                CompletableFuture<Component> fut = offlineFunc.apply(context);
                return fut.thenApply(c -> c == null ? Component.empty() : c);
            }
        };
    }

    static AsyncPlaceholder ofOfflinePlayer(Function<OfflinePlayer, CompletableFuture<Component>> func) {
        return ctx -> {
            CompletableFuture<Component> fut = func.apply(ctx);
            return fut.thenApply(c -> c == null ? Component.empty() : c);
        };
    }

    static AsyncPlaceholder ofPlayer(Function<Player, CompletableFuture<Component>> func) {
        return of(func, offline -> CompletableFuture.completedFuture(Component.empty()));
    }

}
