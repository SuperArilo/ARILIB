package com.tty.lib.enum_type;

public enum LangType implements LangTypeEnum {
    PERIOD_UNRESOLVED("period"),

    PLAYER_NAME_UNRESOLVED("player_name"),

    BAN_T0TAL_TIME("ban_total_time"),
    BAN_REASON("ban_reason"),
    BAN_END_TIME("ban_end_time");

    private final String type;

    LangType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
