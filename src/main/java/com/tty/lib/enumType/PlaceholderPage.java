package com.tty.lib.enumType;

import com.tty.api.enumType.PlaceholderTypeEnum;

public enum PlaceholderPage implements PlaceholderTypeEnum {
    PAGE_TITLE("page_title"),
    PAGE_PREV_TEXT("page_prev_text"),
    PAGE_NEXT_TEXT("page_next_text"),
    CURRENT_PAGE("current_page"),
    TOTAL_PAGE("total_page"),
    TOTAL_DATA_RECORDS("total_data_records");

    private final String type;

    PlaceholderPage(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
