package com.tty.lib.services.placeholder;

import com.tty.lib.enum_type.LangTypeEnum;

public interface PlaceholderDefinition<E, C> {

    E key();
    PlaceholderResolve<C> resolver();

    static <E extends Enum<E> & LangTypeEnum, C> PlaceholderDefinition<E, C> of(E key, PlaceholderResolve<C> resolver) {
        return new PlaceholderDefinition<>() {
            @Override
            public E key() {
                return key;
            }

            @Override
            public PlaceholderResolve<C> resolver() {
                return resolver;
            }
        };
    }

}

