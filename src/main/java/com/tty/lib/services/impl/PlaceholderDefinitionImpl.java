package com.tty.lib.services.impl;

import com.tty.lib.enum_type.LangTypeEnum;
import com.tty.lib.services.placeholder.AsyncPlaceholder;
import com.tty.lib.services.placeholder.PlaceholderDefinition;

record PlaceholderDefinitionImpl<E extends Enum<E> & LangTypeEnum>(E key, AsyncPlaceholder placeholder) implements PlaceholderDefinition<E> {

    @Override
    public AsyncPlaceholder resolver() {
        return this.placeholder;
    }

}
