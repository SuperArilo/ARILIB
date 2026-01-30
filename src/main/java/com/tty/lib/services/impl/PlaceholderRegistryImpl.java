package com.tty.lib.services.impl;

import com.tty.api.enumType.LangTypeEnum;
import com.tty.lib.services.placeholder.PlaceholderResolve;
import com.tty.lib.services.placeholder.PlaceholderDefinition;
import com.tty.lib.services.placeholder.PlaceholderRegistry;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlaceholderRegistryImpl implements PlaceholderRegistry {

    private final Map<String, PlaceholderResolve> placeholders = new HashMap<>();

    @Override
    public void register(PlaceholderDefinition<? extends LangTypeEnum> definition) {
        String key = definition.key().getType();
        if (this.placeholders.containsKey(key)) {
            throw new IllegalStateException("Duplicate placeholder: " + key);
        }
        this.placeholders.put(key, definition.resolver());
    }

    @Override
    public Optional<PlaceholderResolve> find(String key, OfflinePlayer context) {
        return Optional.ofNullable(this.placeholders.get(key));
    }

}
