package com.tty.lib.entity.gui;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class BaseItem implements Serializable {
    private String name;
    private String material;
    private List<Integer> slot;
    private List<String> lore = new ArrayList<>();
}
