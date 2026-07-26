package com.tty.lib.configuration.lang;

import com.tty.api.AbstractJavaPlugin;
import com.tty.api.configuration.AllowDownloadConfiguration;
import com.tty.lib.Lib;
import com.tty.lib.enumType.LangFile;

public class LangConfig extends AllowDownloadConfiguration {

    public LangConfig() {
        super(Lib.instance, LangFile.LANG.getFullPathInJar().replace("[lang]", Lib.instance.getConfig().getString("lang", "cn")));
    }
    public LangConfig(AbstractJavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getDownloadUrl() {
        String base = "https://raw.githubusercontent.com/SuperArilo/Plugin-Configs/main/";
        return base + Lib.instance.getName() + "/" + LangFile.LANG.getFullPathInJar().replace("[lang]", Lib.instance.getConfig().getString("lang", "cn"));
    }

}
