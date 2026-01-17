package com.tty.lib.services.placeholder;

import com.tty.lib.enum_type.LangTypeEnum;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public interface PlaceholderRegistry {

    void register(PlaceholderDefinition<? extends LangTypeEnum> definition);
    Optional<PlaceholderResolve> find(String key, OfflinePlayer context);

}

