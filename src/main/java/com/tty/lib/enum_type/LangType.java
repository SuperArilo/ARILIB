package com.tty.lib.enum_type;

public enum LangType implements LangTypeEnum {
    PERIOD_UNRESOLVED("period"),

    PLAYER_NAME_UNRESOLVED("player_name");

    private final String type;

    LangType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
