package com.tty.lib.services.placeholder;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PlaceholderEngine {

    CompletableFuture<Component> render(String template, OfflinePlayer context);
    CompletableFuture<Component> renderList(List<String> templates, OfflinePlayer context);
    default CompletableFuture<Component> render(String template, Player player) {
        return render(template, (OfflinePlayer) player);
    }

    default CompletableFuture<Component> renderList(List<String> templates, Player player) {
        return renderList(templates, (OfflinePlayer) player);
    }

}
