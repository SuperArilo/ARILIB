package com.tty.lib.services.placeholder;

import com.tty.lib.enum_type.LangTypeEnum;

public interface PlaceholderDefinition<E> {

    E key();
    AsyncPlaceholder resolver();

    static <E extends Enum<E> & LangTypeEnum> PlaceholderDefinition<E> of(E key, AsyncPlaceholder resolver) {
        return new PlaceholderDefinition<>() {
            @Override
            public E key() {
                return key;
            }

            @Override
            public AsyncPlaceholder resolver() {
                return resolver;
            }
        };
    }

}

