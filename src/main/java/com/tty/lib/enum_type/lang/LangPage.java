package com.tty.lib.enum_type.lang;

import com.tty.lib.enum_type.LangTypeEnum;

public enum LangPage implements LangTypeEnum {
    PAGE_TITLE("page_title"),
    PAGE_PREV_TEXT("page_prev_text"),
    PAGE_NEXT_TEXT("page_next_text"),
    CURRENT_PAGE("current_page"),
    TOTAL_PAGE("total_page"),
    TOTAL_DATA_RECORDS("total_data_records");

    private final String type;

    LangPage(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
