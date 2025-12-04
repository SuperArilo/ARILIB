package com.tty.lib.tool;


import java.util.HashMap;
import java.util.Map;

public class ColorConverterLegacy {

    private static final Map<Character, String> LEGACY_MAP = new HashMap<>();

    static {
        LEGACY_MAP.put('0', "black");
        LEGACY_MAP.put('1', "dark_blue");
        LEGACY_MAP.put('2', "dark_green");
        LEGACY_MAP.put('3', "dark_aqua");
        LEGACY_MAP.put('4', "dark_red");
        LEGACY_MAP.put('5', "dark_purple");
        LEGACY_MAP.put('6', "gold");
        LEGACY_MAP.put('7', "gray");
        LEGACY_MAP.put('8', "dark_gray");
        LEGACY_MAP.put('9', "blue");
        LEGACY_MAP.put('a', "green");
        LEGACY_MAP.put('b', "aqua");
        LEGACY_MAP.put('c', "red");
        LEGACY_MAP.put('d', "light_purple");
        LEGACY_MAP.put('e', "yellow");
        LEGACY_MAP.put('f', "white");
        LEGACY_MAP.put('r', "reset");
    }

    public static String convert(String input) {
        StringBuilder sb = new StringBuilder();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if ((c == '&' || c == '§') && i + 1 < chars.length) {
                char code = Character.toLowerCase(chars[i + 1]);
                String mmColor = LEGACY_MAP.get(code);
                if (mmColor != null) {
                    sb.append("<").append(mmColor).append(">");
                    i++;
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
