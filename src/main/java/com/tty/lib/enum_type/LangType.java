package com.tty.lib.enum_type;

public enum LangType implements LangTypeEnum {
    PERIOD_UNRESOLVED("period"),
    SOURCE_DISPLAY_NAME_UNRESOLVED("source_display_name"),
    CHAT_MESSAGE_UNRESOLVED("message"),
    PLAYER_NAME_UNRESOLVED("player_name"),

    KILLER("killer"),
    VICTIM("victim"),
    KILLER_ITEM("killer_item"),
    BAN_T0TAL_TIME("ban_total_time"),
    BAN_REASON("ban_reason"),
    BAN_END_TIME("ban_end_time"),
    MOB("mob"),
    MOB_CURRENT_HEALTH("mob_current_health"),
    MOB_MAX_HEALTH("mob_max_health"),
    MAINTENANCE_KICK_DEALY("maintenance_kick_delay"),
    ENCHANT_NAME("enchant_name"),
    ENCHANT_LEVEL("enchant_level");

    private final String type;

    LangType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
