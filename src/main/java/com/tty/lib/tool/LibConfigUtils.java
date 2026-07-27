package com.tty.lib.tool;

import com.tty.lib.Lib;
import com.tty.lib.configuration.lang.LangConfig;
import net.kyori.adventure.text.Component;

import java.util.concurrent.CompletableFuture;

public class LibConfigUtils {

    /**
     * 快捷访问 Lang
     * @param key 在 lang 中对应的 key 路径
     * @return 返回构建完成的 Component
     */
    public static Component t(String key) {
        return Lib.instance.getEngine().directRender(Lib.instance.getConfigurationManager().get(LangConfig.class).getValue(key, String.class, "null"));
    }

    public static CompletableFuture<Component> tList(String key) {
        return Lib.instance.getEngine().renderList(Lib.instance.getConfigurationManager().get(LangConfig.class).getStringList(key), null);
    }

}
