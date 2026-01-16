package com.tty.lib.services.placeholder;

import com.tty.lib.enum_type.LangTypeEnum;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public interface PlaceholderRegistry {

    <E extends Enum<E> & LangTypeEnum> void register(PlaceholderDefinition<E> definition);
    Optional<Placeholder> find(String key, OfflinePlayer context);

}

