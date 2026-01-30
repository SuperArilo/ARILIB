package com.tty.lib.services.placeholder;

import com.tty.api.enumType.LangTypeEnum;

public interface PlaceholderDefinition<E> {

    E key();
    PlaceholderResolve resolver();

    static <E extends Enum<E> & LangTypeEnum> PlaceholderDefinition<E> of(E key, PlaceholderResolve resolver) {
        return new PlaceholderDefinition<>() {
            @Override
            public E key() {
                return key;
            }

            @Override
            public PlaceholderResolve resolver() {
                return resolver;
            }
        };
    }

}

