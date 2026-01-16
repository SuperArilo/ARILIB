package com.tty.lib.services.placeholder;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.function.Function;

public interface SyncPlaceholder extends Placeholder {

    Component resolve(OfflinePlayer context);

    static SyncPlaceholder of(Function<Player, Component> pFc, Function<OfflinePlayer, Component> oFc) {
        return context -> {
            if (context instanceof Player player) {
                return pFc.apply(player);
            } else {
                return oFc.apply(context);
            }
        };
    }

    static SyncPlaceholder ofOfflinePlayer(Function<OfflinePlayer, Component> function) {
        return function::apply;
    }

    static SyncPlaceholder ofPlayer(Function<Player, Component> function) {
        return of(function, offlinePlayer -> Component.empty());
    }
}
