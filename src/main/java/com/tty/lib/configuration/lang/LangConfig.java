package com.tty.lib.configuration.lang;

import com.tty.api.configuration.LangConfiguration;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.LangFile;

public class LangConfig extends LangConfiguration {
    public LangConfig() {
        super(Lib.instance, LangFile.LANG.getPath());
    }
}
