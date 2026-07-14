package com.tty.lib.tool;

import com.tty.lib.Lib;
import com.tty.lib.configuration.lang.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.concurrent.CompletableFuture;

public class LibConfigUtils {

    /**
     * 快捷访问 Lang
     * @param key 在 lang 中对应的 key 路径
     * @return 返回构建完成的 Component
     */
    public static TextComponent t(String key) {
        return Lib.instance.getComponentTool().text(Lib.instance.getConfigurationManager().get(LangConfig.class).getValue(key, String.class, "null"));
    }

    public static CompletableFuture<Component> tList(String key) {
        return Lib.PLACEHOLDER.renderList(key, null);
    }

}
