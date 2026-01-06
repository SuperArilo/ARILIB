package com.tty.lib.services.impl;

import com.tty.lib.Lib;
import com.tty.lib.dto.ComponentListPage;
import com.tty.lib.enum_type.FilePath;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.services.ConfigDataService;
import com.tty.lib.tool.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.HashMap;
import java.util.Map;

public class ConfigDataServiceImpl implements ConfigDataService {

    @Override
    public String getValue(String keyPath) {
        return Lib.C_INSTANCE.getValue(keyPath, FilePath.Lang);
    }

    @Override
    public ComponentListPage createComponentDataPage(Component titleName, String prevAction, String nextAction, Integer currentPage, Integer totalPage) {
        ComponentListPage page = new ComponentListPage();
        TextComponent title = ComponentUtils.text(Lib.C_INSTANCE.getValue("base.page.line-start", FilePath.Lang), Map.of(LangType.PAGE_TITLE.getType(), titleName));
        page.setTitle(title);

        TextComponent prev = null;
        if (prevAction != null) {
            prev = ComponentUtils.setClickEventText(Lib.C_INSTANCE.getValue("base.page.prev", FilePath.Lang), ClickEvent.Action.RUN_COMMAND, prevAction);
        }

        TextComponent next = null;
        if (nextAction != null) {
            next = ComponentUtils.setClickEventText(Lib.C_INSTANCE.getValue("base.page.next", FilePath.Lang), ClickEvent.Action.RUN_COMMAND, nextAction);
        }


        HashMap<String, Component> map = new HashMap<>();
        map.put(LangType.PAGE_PREV.getType(), prev);
        map.put(LangType.PAGE_NEXT.getType(), next);
        map.put(LangType.CURRENT_PAGE.getType(), Component.text(currentPage));
        map.put(LangType.TOTAL_PAGE.getType(), Component.text(totalPage));

        TextComponent end = ComponentUtils.text(Lib.CONFIG_DATA_SERVICE.getValue("base.page.line-end"), map);

        page.setFooter(end);
        return page;
    }

}
