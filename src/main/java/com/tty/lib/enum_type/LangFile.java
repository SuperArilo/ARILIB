package com.tty.lib.enum_type;

import com.tty.api.enumType.FilePathEnum;

public enum LangFile implements FilePathEnum {

    LANG("lang/[lang].yml");

    private final String filePath;

    LangFile(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getPath() {
        return this.filePath;
    }
}
