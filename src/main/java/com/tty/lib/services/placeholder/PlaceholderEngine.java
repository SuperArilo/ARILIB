package com.tty.lib.services.placeholder;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PlaceholderEngine {

    CompletableFuture<Component> renderAsync(String template, OfflinePlayer context);
    CompletableFuture<Component> renderListAsync(List<String> templates, OfflinePlayer context);

    default CompletableFuture<Component> renderAsync(String template, Player player) {
        return renderAsync(template, (OfflinePlayer) player);
    }
    default CompletableFuture<Component> renderListAsync(List<String> templates, Player player) {
        return renderListAsync(templates, (OfflinePlayer) player);
    }

}
