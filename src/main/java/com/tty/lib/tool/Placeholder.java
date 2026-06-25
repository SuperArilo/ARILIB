package com.tty.lib.tool;

import com.tty.api.AbstractJavaPlugin;
import com.tty.api.service.impl.PlaceholderRegistryImpl;
import com.tty.api.service.placeholder.BasePlaceholder;
import com.tty.api.service.placeholder.PlaceholderRegistry;
import com.tty.lib.enum_type.LangFile;


public class Placeholder extends BasePlaceholder<LangFile> {

    public Placeholder(AbstractJavaPlugin plugin) {
        super(plugin, LangFile.LANG);
    }

    public void init() {
        PlaceholderRegistryImpl registry = new PlaceholderRegistryImpl();
    }

    private void registerList(PlaceholderRegistry registry) {

    }

}
