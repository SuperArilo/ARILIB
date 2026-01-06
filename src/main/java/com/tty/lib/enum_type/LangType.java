package com.tty.lib.enum_type;

public enum LangType implements LangTypeEnum {
    TPA_SENDER("tpa_sender"),
    TPAHERE_SENDER("tpahere_sender"),
    TPA_BE_SENDER("tpa_be_sender"),
    COSTED("costed"),
    TIME("time"),
    DEATH_LOCATION("death_location"),
    PERIOD("period"),
    SLEEP_PLAYERS("sleep_players"),
    SKIP_NIGHT_TICK_INCREMENT("skip_night_tick_increment"),
    SPAWN_LOCATION("spawn_location"),
    SOURCE_DISPLAY_NAME("source_display_name"),
    CHAT_MESSAGE("message"),
    RTP_SEARCH_COUNT("rtp_search_count"),
    TELEPORT_DELAY("teleport_delay"),
    PLAYER_NAME("player_name"),
    FIRST_LOGIN_SERVER_TIME("first_login_server_time"),
    LAST_LOGIN_SERVER_TIME("last_login_server_time"),
    TOTAL_ON_SERVER("total_on_server"),
    PLAYER_WORLD("player_world"),
    PLAYER_LOCATION("player_location"),
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
    PAGE_TITLE("page_title"),
    PAGE_PREV_TEXT("page_prev_text"),
    PAGE_NEXT_TEXT("page_next_text"),
    CURRENT_PAGE("current_page"),
    TOTAL_PAGE("total_page");

    private final String type;

    LangType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
