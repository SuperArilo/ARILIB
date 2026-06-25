package com.tty.lib.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tty.lib.Lib;
import com.tty.api.dto.ComponentListPage;
import com.tty.lib.enum_type.LangFile;
import com.tty.lib.enum_type.PlaceholderPage;
import com.tty.api.service.ConfigDataService;
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
        return Lib.instance.getConfigInstance().getValue(keyPath, LangFile.LANG);
    }

    @Override
    public ComponentListPage createComponentDataPage(Component titleName, String prevAction, String nextAction, Integer currentPage, Integer totalPage, Integer totalRecords) {
        ComponentListPage page = new ComponentListPage();
        TextComponent title = Lib.instance.getComponentTool().text(Lib.instance.getConfigInstance().getValue("base.page.line-start", LangFile.LANG), Map.of(PlaceholderPage.PAGE_TITLE.getType(), titleName));
        page.setTitle(title);

        TextComponent prev = null;
        if (prevAction != null) {
            prev = Lib.instance.getComponentTool().setClickEventText(Lib.instance.getConfigInstance().getValue("base.page.prev", LangFile.LANG), ClickEvent.Action.RUN_COMMAND, prevAction);
        }

        TextComponent next = null;
        if (nextAction != null) {
            next = Lib.instance.getComponentTool().setClickEventText(Lib.instance.getConfigInstance().getValue("base.page.next", LangFile.LANG), ClickEvent.Action.RUN_COMMAND, nextAction);
        }


        HashMap<String, Component> map = new HashMap<>();
        map.put(PlaceholderPage.PAGE_PREV_TEXT.getType(), prev);
        map.put(PlaceholderPage.PAGE_NEXT_TEXT.getType(), next);
        map.put(PlaceholderPage.CURRENT_PAGE.getType(), Component.text(currentPage));
        map.put(PlaceholderPage.TOTAL_PAGE.getType(), Component.text(totalPage));
        map.put(PlaceholderPage.TOTAL_DATA_RECORDS.getType(), Component.text(totalRecords));

        TextComponent end = Lib.instance.getComponentTool().text(Lib.CONFIG_DATA_SERVICE.getValue("base.page.line-end"), map);

        page.setFooter(end);
        return page;
    }

}
