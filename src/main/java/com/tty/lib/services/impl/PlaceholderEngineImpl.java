package com.tty.lib.services.impl;

import com.tty.lib.Lib;
import com.tty.lib.services.placeholder.PlaceholderEngine;
import com.tty.lib.services.placeholder.PlaceholderRegistry;
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

    private static final Pattern PATTERN = Pattern.compile("<([a-z0-9_]+)>");

    @Setter
    @Getter
    private PlaceholderRegistry registry;

    @Override
    public CompletableFuture<Component> render(String template, OfflinePlayer context) {

        Matcher matcher = PATTERN.matcher(template);
        Map<String, CompletableFuture<Component>> futures = new HashMap<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            if (futures.containsKey(key)) continue;
            registry.find(key, context).ifPresent(resolver -> futures.put(key, resolver.resolve(context)));
        }

        if (futures.isEmpty()) {
            return CompletableFuture.completedFuture(Lib.COMPONENT_SERVICE.text(template, context, Collections.emptyMap()));
        }

        CompletableFuture<?>[] all = futures.values().toArray(new CompletableFuture[0]);

        return CompletableFuture.allOf(all).thenApply(v -> {
            Map<String, Component> resolved = new HashMap<>(futures.size());
            futures.forEach((k, f) -> resolved.put(k, f.join()));
            return Lib.COMPONENT_SERVICE.text(template, context, resolved);
        });
    }

    @Override
    public CompletableFuture<Component> renderList(List<String> list, OfflinePlayer context) {

        Set<String> keys = new HashSet<>();
        for (String line : list) {
            Matcher matcher = PATTERN.matcher(line);
            while (matcher.find()) {
                keys.add(matcher.group(1));
            }
        }

        Map<String, CompletableFuture<Component>> futures = new HashMap<>(keys.size());
        for (String key : keys) {
            registry.find(key, context).ifPresent(resolver -> futures.put(key, resolver.resolve(context)));
        }

        if (futures.isEmpty()) {
            List<Component> components = new ArrayList<>(list.size());
            for (String line : list) {
                components.add(Lib.COMPONENT_SERVICE.text(line, context, Collections.emptyMap()));
            }
            return CompletableFuture.completedFuture(Component.join(JoinConfiguration.separator(Component.newline()), components));
        }

        CompletableFuture<?>[] all = futures.values().toArray(new CompletableFuture[0]);

        return CompletableFuture.allOf(all).thenApply(v -> {
            Map<String, Component> resolved = new HashMap<>(futures.size());
            futures.forEach((k, f) -> resolved.put(k, f.join()));
            List<Component> components = new ArrayList<>(list.size());
            for (String line : list) {
                components.add(Lib.COMPONENT_SERVICE.text(line, context, resolved));
            }
            return Component.join(JoinConfiguration.separator(Component.newline()), components);
        });
    }
}
