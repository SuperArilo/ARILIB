package com.tty.lib.tool;
import com.google.gson.Gson;
import com.tty.lib.Log;
import com.tty.lib.enum_type.FunctionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class FormatUtils {

    /**
     * 格式化数字保留两位小数
     * @param number 需要格式化的数字（支持所有Number子类）
     * @return 格式化后的字符串，输入为null时返回"0.00"
     */
    public static String formatTwoDecimalPlaces(Number number) {
        if (number == null) return "0.00";
        return ThreadLocal.withInitial(() -> new DecimalFormat("0.00")).get().format(number);
    }


    /**
     * 检查ID名称合法性（字母数字下划线）
     * @param content 待检查字符串
     * @return 空值或不符合格式返回false
     */
    public static boolean checkIdName(String content) {
        return content != null && content.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * 检查名称合法性（支持中文字符）
     * @param content 待检查字符串
     * @return 空值或不符合格式返回false
     */
    public static boolean checkName(String content) {
        return content != null && content.matches("^[a-zA-Z0-9\\u4e00-\\u9fa5]+$");
    }

    /**
     * 验证Minecraft权限节点格式
     * @param node 权限节点字符串
     * @return 空值或不符合格式返回false
     */
    public static boolean isValidPermissionNode(String node) {
        return node != null && node.matches("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$");
    }

    /**
     * 将 Component 转成 String
     * @param component 被转对象
     * @return 返回String
     */
    public static String componentToString(Component component) {
        if (component == null) return "";
        if(component instanceof TextComponent) {
            return ((TextComponent) component).content();
        }
        return component.toString();
    }

    /**
     * 返回 基础格式化的文本坐标
     * @param x x轴
     * @param y y轴
     * @param z z轴
     * @return 返回基础格式化的文本坐标
     */
    public static String XYZText(Double x, Double y, Double z) {
        return "&2x: &6" + FormatUtils.formatTwoDecimalPlaces(x) +
                " &2y: &6" + FormatUtils.formatTwoDecimalPlaces(y) +
                " &2z: &6" + FormatUtils.formatTwoDecimalPlaces(z);
    }

    /**
     * 将 string 的 Location 转换成 Location对象
     * @param locString string
     * @return Location 对象
     */
    public static Location parseLocation(String locString) {
        if (locString == null || locString.isEmpty()) {
            throw new IllegalArgumentException("Location string is null or empty!");
        }

        // 去掉 Location{...} 外层
        if (locString.startsWith("Location{") && locString.endsWith("}")) {
            locString = locString.substring(9, locString.length() - 1);
        }

        String worldName = null;
        double x = 0, y = 0, z = 0;
        float pitch = 0, yaw = 0;

        int len = locString.length();
        int i = 0;

        while (i < len) {
            // 找 key
            int eqIdx = locString.indexOf('=', i);
            if (eqIdx < 0) break;

            String key = locString.substring(i, eqIdx).trim();

            // 找 value 结束位置
            int valEnd = locString.indexOf(',', eqIdx + 1);
            if (valEnd < 0) valEnd = len;

            String value = locString.substring(eqIdx + 1, valEnd).trim();

            switch (key) {
                case "world":
                    // 支持 CraftWorld{name=world}
                    if (value.startsWith("CraftWorld")) {
                        int nameIdx = value.indexOf("name=");
                        if (nameIdx >= 0) {
                            int nameStart = nameIdx + 5;
                            int nameEnd = value.indexOf('}', nameStart);
                            if (nameEnd < 0) nameEnd = value.length();
                            worldName = value.substring(nameStart, nameEnd);
                        } else {
                            worldName = value;
                        }
                    } else {
                        worldName = value;
                    }
                    break;
                case "x": x = Double.parseDouble(value); break;
                case "y": y = Double.parseDouble(value); break;
                case "z": z = Double.parseDouble(value); break;
                case "pitch": pitch = Float.parseFloat(value); break;
                case "yaw": yaw = Float.parseFloat(value); break;
            }

            i = valEnd + 1;
        }

        if (worldName == null) {
            throw new IllegalArgumentException("World not found in location string: " + locString);
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World not found on server: " + worldName);
        }

        return new Location(world, x, y, z, yaw, pitch);
    }


    /**
     * 将 MemorySection 转成 YamlConfiguration
     * @param source MemorySection
     * @param target YamlConfiguration
     */
    public static void copySectionToYamlConfiguration(ConfigurationSection source, ConfigurationSection target) {
        Map<String, Object> values = source.getValues(false);

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof ConfigurationSection) {
                ConfigurationSection newSection = target.createSection(key);
                copySectionToYamlConfiguration((ConfigurationSection) value, newSection);
            } else {
                target.set(key, value);
            }
        }
    }

    /**
     * 将 yaml 的字符串转换成指定类型
     * @param raw yaml 字符串
     * @param type 转换的目标类型
     * @return 返回 的 type 类型
     */
    public static <T> T yamlConvertToObj(String raw, Type type) {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowRecursiveKeys(true);
        loaderOptions.setAllowDuplicateKeys(false);
        Gson gson = new Gson();
        Object intermediateObj = new Yaml(loaderOptions).load(raw);
        if (intermediateObj instanceof Map || intermediateObj instanceof List) {
            return gson.fromJson(gson.toJson(intermediateObj), type);
        }
        return gson.fromJson(gson.toJsonTree(intermediateObj), type);
    }

    /**
     * 检查GUI里的 function icon 是否合法
     * @param rawType function icon 的字符串
     * @return 返回指定的 FunctionType
     */
    public static FunctionType ItemNBT_TypeCheck(String rawType) {
        if(rawType == null) return null;
        FunctionType type;
        try {
            type = FunctionType.valueOf(rawType.toUpperCase());
            return type;
        } catch (Exception e) {
            Log.debug(e, "Function type {} error", rawType);
            return null;
        }
    }

}
