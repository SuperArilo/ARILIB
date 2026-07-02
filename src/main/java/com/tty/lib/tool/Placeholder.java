package com.tty.lib.tool;

import com.tty.api.AbstractJavaPlugin;
import com.tty.api.service.impl.PlaceholderRegistryImpl;
import com.tty.api.service.placeholder.BasePlaceholder;
import com.tty.api.service.placeholder.PlaceholderRegistry;
import com.tty.lib.Lib;
import com.tty.lib.configuration.lang.LangConfig;


public class Placeholder extends BasePlaceholder {

    public Placeholder(AbstractJavaPlugin plugin) {
        super(plugin, Lib.instance.getConfigurationManager().get(LangConfig.class));
    }

    public void init() {
        PlaceholderRegistryImpl registry = new PlaceholderRegistryImpl();
    }

    private void registerList(PlaceholderRegistry registry) {

    }

}
