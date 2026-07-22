package com.tty.lib.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tty.api.ComponentTool;
import com.tty.api.dto.ComponentListPage;
import com.tty.api.service.ConfigDataService;
import com.tty.lib.Lib;
import com.tty.lib.configuration.lang.LangConfig;
import com.tty.lib.enumType.PlaceholderPage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ConfigDataServiceImpl implements ConfigDataService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public <T> T getValue(@NotNull String kePath, @NotNull Type type) {
        Object object = Lib.instance.getConfig().get(kePath);
        return this.gson.fromJson(this.gson.toJsonTree(object), type);
    }

    @Override
    public String getValue(String keyPath) {
        return Lib.instance.getConfigurationManager().get(LangConfig.class).getValue(keyPath, String.class, "null");
    }

    @Override
    public ComponentListPage createComponentDataPage(Component titleName, String prevAction, String nextAction, Integer currentPage, Integer totalPage, Integer totalRecords) {

        LangConfig langConfig = Lib.instance.getConfigurationManager().get(LangConfig.class);

        ComponentListPage page = new ComponentListPage();
        TextComponent title = ComponentTool.text(langConfig.getValue("base.page.line-start", String.class, "null"), Map.of(PlaceholderPage.PAGE_TITLE.getType(), titleName));
        page.setTitle(title);

        TextComponent prev = null;
        if (prevAction != null) {
            prev = ComponentTool.setClickEventText(langConfig.getValue("base.page.prev", String.class, "null"), ClickEvent.runCommand(prevAction));
        }

        TextComponent next = null;
        if (nextAction != null) {
            next = ComponentTool.setClickEventText(langConfig.getValue("base.page.next", String.class, "null"), ClickEvent.runCommand(nextAction));
        }


        HashMap<String, Component> map = new HashMap<>();
        map.put(PlaceholderPage.PAGE_PREV_TEXT.getType(), prev);
        map.put(PlaceholderPage.PAGE_NEXT_TEXT.getType(), next);
        map.put(PlaceholderPage.CURRENT_PAGE.getType(), Component.text(currentPage));
        map.put(PlaceholderPage.TOTAL_PAGE.getType(), Component.text(totalPage));
        map.put(PlaceholderPage.TOTAL_DATA_RECORDS.getType(), Component.text(totalRecords));

        TextComponent end = ComponentTool.text(Lib.CONFIG_DATA_SERVICE.getValue("base.page.line-end"), map);

        page.setFooter(end);
        return page;
    }

}
