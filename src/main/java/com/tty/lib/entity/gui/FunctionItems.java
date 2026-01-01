package com.tty.lib.entity.gui;

import com.tty.lib.enum_type.FunctionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FunctionItems extends BaseItem {
    private FunctionType type;
}
