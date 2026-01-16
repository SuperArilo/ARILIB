package com.tty.lib.services.impl;

import com.tty.lib.Log;
import com.tty.lib.services.placeholder.AsyncPlaceholder;
import com.tty.lib.services.placeholder.PlaceholderEngine;
import com.tty.lib.services.placeholder.PlaceholderRegistry;
import com.tty.lib.services.placeholder.SyncPlaceholder;
import com.tty.lib.tool.ComponentUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderEngineImpl implements PlaceholderEngine {

    private final Pattern PATTERN = Pattern.compile("<([a-z0-9_]+)>");

    @Setter
    @Getter
    private PlaceholderRegistry registry;

    @Override
    public Component render(String template, OfflinePlayer context) {

        Matcher matcher = PATTERN.matcher(template);
        Map<String, Component> map = new HashMap<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            registry.find(key, context).ifPresent(resolver -> {
                if (resolver instanceof SyncPlaceholder syncPlaceholder) {
                    map.putIfAbsent(key, syncPlaceholder.resolve(context));
                } else {
                    Log.error("[ {} ] render component error.", this.getClass().getSimpleName());
                }
            });
        }

        return ComponentUtils.text(template, map);
    }

    @Override
    public Component renderList(List<String> templates, OfflinePlayer context) {

        List<Component> components = new ArrayList<>();

        for (String line : templates) {
            components.add(render(line, context));
        }

        return Component.join(JoinConfiguration.separator(Component.newline()), components);
    }

    @Override
    public CompletableFuture<Component> renderAsync(String template, OfflinePlayer context) {

        Matcher matcher = PATTERN.matcher(template);
        Map<String, CompletableFuture<Component>> futures = new HashMap<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            registry.find(key, context).ifPresent(resolver -> {
                if (resolver instanceof AsyncPlaceholder asyncPlaceholder) {
                    futures.putIfAbsent(key, asyncPlaceholder.resolve(context));
                } else {
                    Log.error("[ {} ] render component error.", this.getClass().getSimpleName());
                }
            });
        }

        CompletableFuture<?>[] all = futures.values().toArray(new CompletableFuture[0]);

        return CompletableFuture.allOf(all).thenApply(v -> {
            Map<String, Component> map = new HashMap<>();
            futures.forEach((k, f) ->map.put(k, f.join()));
            return ComponentUtils.text(template, map);
        });
    }

    @Override
    public CompletableFuture<Component> renderListAsync(List<String> list, OfflinePlayer context) {
        List<CompletableFuture<Component>> futures = list.stream().map(line -> renderAsync(line, context)).toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(v ->
                Component.join(JoinConfiguration.separator(Component.newline()), futures.stream().map(CompletableFuture::join).toList())
        );
    }

}
