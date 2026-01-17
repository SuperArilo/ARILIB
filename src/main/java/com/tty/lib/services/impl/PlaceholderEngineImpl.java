package com.tty.lib.services.impl;

import com.tty.lib.services.placeholder.PlaceholderEngine;
import com.tty.lib.services.placeholder.PlaceholderRegistry;
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
    public CompletableFuture<Component> render(String template, OfflinePlayer context) {

        Matcher matcher = PATTERN.matcher(template);
        Map<String, CompletableFuture<Component>> futures = new HashMap<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            registry.find(key, context).ifPresent(resolver -> futures.putIfAbsent(key, resolver.resolve(context)));
        }

        CompletableFuture<?>[] all = futures.values().toArray(new CompletableFuture[0]);

        return CompletableFuture.allOf(all).thenApply(v -> {
            Map<String, Component> map = new HashMap<>();
            futures.forEach((k, f) ->map.put(k, f.join()));
            return ComponentUtils.text(template, map);
        });
    }

    @Override
    public CompletableFuture<Component> renderList(List<String> list, OfflinePlayer context) {
        List<CompletableFuture<Component>> futures = list.stream().map(line -> render(line, context)).toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(v ->
                Component.join(JoinConfiguration.separator(Component.newline()), futures.stream().map(CompletableFuture::join).toList())
        );
    }

}
