package com.tty.lib.services.placeholder;

public interface PlaceholderDefinition<E> {

    E key();
    AsyncPlaceholder resolver();

}

