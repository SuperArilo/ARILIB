package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum IconKeyType {
    ID("id"),
    X("x"),
    Y("y"),
    Z("z"),
    WORLD_NAME("world_name"),
    PLAYER_NAME("player_name"),
    COST("cost"),
    TOP_SLOT("top_slot"),
    PERMISSION("permission");

    private final String key;

    IconKeyType(String key) {
        this.key = key;
    }

}
