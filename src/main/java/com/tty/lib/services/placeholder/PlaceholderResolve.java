package com.tty.lib.services.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.function.Function;

@FunctionalInterface
public interface PlaceholderResolve<T> {

    T resolve(OfflinePlayer context);

    static <T> PlaceholderResolve<T> of(Function<Player, T> playerFunc, Function<OfflinePlayer, T> offlineFunc) {
        return context -> {
            if (context instanceof Player player) {
                return playerFunc.apply(player);
            } else {
                return offlineFunc.apply(context);
            }
        };
    }

    static <T> PlaceholderResolve<T> ofPlayer(Function<Player, T> function) {
        return of(function, offlinePlayer -> null);
    }

    static <T> PlaceholderResolve<T> ofOfflinePlayer(Function<OfflinePlayer, T> function) {
        return function::apply;
    }

}
