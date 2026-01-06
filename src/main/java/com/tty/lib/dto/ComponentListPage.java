package com.tty.lib.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kyori.adventure.text.Component;

@EqualsAndHashCode(callSuper = true)
@Data
public class ComponentListPage extends ComponentList {

    private Component footer;

    public Component build() {
        Component build = super.build();
        return build.appendNewline().append(this.footer);
    }

}
