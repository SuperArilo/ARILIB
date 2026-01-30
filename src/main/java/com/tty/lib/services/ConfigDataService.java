package com.tty.lib.services;

import com.tty.api.dto.ComponentListPage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public interface ConfigDataService {

    <T> T getValue(@NotNull String kePath, @NotNull Type type);
    String getValue(String keyPath);
    ComponentListPage createComponentDataPage(Component titleName, @Nullable String prevAction, @Nullable String nextAction, Integer currentPage, Integer totalPage, Integer totalRecords);
}
