package com.tty.lib.services;

import com.tty.lib.dto.ComponentListPage;
import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;

public interface ConfigDataService extends BaseDataService {
    String getValue(String keyPath);
    ComponentListPage createComponentDataPage(Component titleName, @Nullable String prevAction, @Nullable String nextAction);
}
