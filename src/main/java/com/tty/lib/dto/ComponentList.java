package com.tty.lib.dto;

import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;

@Data
public class ComponentList {

    protected Component title;
    protected List<Component> lines = new ArrayList<>();

    protected Component build() {

        TextComponent component = Component.empty();
        component = component.append(this.title);

        for (Component line : this.lines) {
            component = component.appendNewline().append(line);
        }
        return component;
    }

    public void addLine(Component component) {
        this.lines.add(component);
    }

}
