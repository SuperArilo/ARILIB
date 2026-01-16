package com.tty.lib.services.placeholder;

import com.tty.lib.enum_type.LangTypeEnum;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public interface PlaceholderRegistry<C> {

    void register(PlaceholderDefinition<? extends LangTypeEnum, C> definition);
    Optional<PlaceholderResolve<C>> find(String key, OfflinePlayer context);

}

