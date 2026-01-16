package com.tty.lib.services.placeholder;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PlaceholderEngine {

    Component render(String template, OfflinePlayer context);

    Component renderList(List<String> templates, OfflinePlayer context);

    CompletableFuture<Component> renderAsync(String template, OfflinePlayer context);
    CompletableFuture<Component> renderListAsync(List<String> templates, OfflinePlayer context);

    default Component render(String template, Player player) {
        return render(template, (OfflinePlayer) player);
    }

    default Component renderList(List<String> templates, Player player) {
        return renderList(templates, (OfflinePlayer) player);
    }

    default CompletableFuture<Component> renderAsync(String template, Player player) {
        return renderAsync(template, (OfflinePlayer) player);
    }
    default CompletableFuture<Component> renderListAsync(List<String> templates, Player player) {
        return renderListAsync(templates, (OfflinePlayer) player);
    }

}
