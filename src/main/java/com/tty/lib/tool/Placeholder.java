package com.tty.lib.tool;

import com.tty.lib.Lib;
import com.tty.lib.enum_type.FilePath;
import com.tty.lib.services.impl.PlaceholderRegistryImpl;
import com.tty.lib.services.placeholder.BasePlaceholder;
import com.tty.lib.services.placeholder.PlaceholderRegistry;


public class Placeholder extends BasePlaceholder<FilePath> {

    public Placeholder() {
        super(Lib.C_INSTANCE, FilePath.Lang);
    }

    public void init() {
        PlaceholderRegistryImpl registry = new PlaceholderRegistryImpl();
    }

    private void registerList(PlaceholderRegistry registry) {

    }

}
