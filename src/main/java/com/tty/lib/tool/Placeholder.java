package com.tty.lib.tool;

import com.tty.api.AbstractJavaPlugin;
import com.tty.api.service.impl.PlaceholderRegistryImpl;
import com.tty.api.service.placeholder.BasePlaceholder;
import com.tty.api.service.placeholder.PlaceholderRegistry;
import com.tty.lib.enum_type.FilePath;


public class Placeholder extends BasePlaceholder<FilePath> {

    public Placeholder(AbstractJavaPlugin plugin) {
        super(plugin, FilePath.Lang);
    }

    public void init() {
        PlaceholderRegistryImpl registry = new PlaceholderRegistryImpl();
    }

    private void registerList(PlaceholderRegistry registry) {

    }

}
