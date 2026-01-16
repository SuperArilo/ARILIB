package com.tty.lib.services.impl;

import com.tty.lib.enum_type.LangTypeEnum;
import com.tty.lib.services.placeholder.Placeholder;
import com.tty.lib.services.placeholder.PlaceholderDefinition;
import com.tty.lib.services.placeholder.PlaceholderRegistry;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlaceholderRegistryImpl implements PlaceholderRegistry {

    private final Map<String, Placeholder> placeholders = new HashMap<>();

    @Override
    public <E extends Enum<E> & LangTypeEnum> void register(PlaceholderDefinition<E> definition) {
        String key = definition.key().getType();
        if (this.placeholders.containsKey(key)) {
            throw new IllegalStateException("Duplicate placeholder: " + key);
        }
        this.placeholders.put(key, definition.resolver());
    }

    @Override
    public Optional<Placeholder> find(String key, OfflinePlayer context) {
        return Optional.ofNullable(this.placeholders.get(key));
    }

}
