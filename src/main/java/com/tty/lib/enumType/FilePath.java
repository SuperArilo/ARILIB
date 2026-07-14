package com.tty.lib.enumType;

import com.tty.api.enumType.FilePathEnum;

public enum FilePath implements FilePathEnum {

    ;

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

}
