package com.tty.lib.enumType;

import com.tty.api.enumType.FilePathEnum;

public enum FilePath implements FilePathEnum {

    ;

    private final String fullPathInJar;
    private final String fullFileName;

    FilePath(String fullPathInJar, String fullFileName) {
        this.fullPathInJar = fullPathInJar;
        this.fullFileName = fullFileName;
    }

    @Override
    public String getFullPathInJar() {
        return this.fullPathInJar;
    }

    @Override
    public String getFullFileName() {
        return this.fullFileName;
    }

}
