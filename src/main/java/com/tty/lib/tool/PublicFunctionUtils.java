package com.tty.lib.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tty.lib.Log;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PublicFunctionUtils {

    public static <T> void loadPlugin(String pluginName, Class<T> tClass, Consumer<T> consumer) {
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            RegisteredServiceProvider<T> registration = Bukkit.getServer().getServicesManager().getRegistration(tClass);
            if (registration != null) {
                consumer.accept(registration.getProvider());
            } else {
                Log.warn("failed to load plugin: {}. because {} is null", pluginName, pluginName);
            }
        } else {
            Log.warn("failed to load plugin: {}.", pluginName);
        }
    }

    public static <T> T deepCopy(T obj, Type typeOfT) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(gson.toJson(obj), typeOfT);
    }

    /**
     * 检查材质是否是ITEM
     * @param material 被检查的材质
     * @return 返回一个正确的材质
     */
    public static Material checkIsItem(Material material) {
        if(!material.isItem() || !material.isSolid()) {
            return Material.DIRT;
        }
        return material;
    }

    /**
     * 随机得到指定范围内的随机数
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int randomGenerator(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("The maximum value must be greater than the minimum value");
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static boolean checkServerVersion() {
        boolean versionAtLeast = isVersionAtLeast(Bukkit.getServer().getBukkitVersion().split("-")[0]);
        if (!versionAtLeast) {
            Bukkit.getLogger().log(Level.SEVERE, "Server version is too low. This plugin requires at least 1.21.3. Disabling plugin...");
            return false;
        }
        return true;
    }

    private static boolean isVersionAtLeast(String current) {
        String[] c = current.split("\\.");
        String[] r = "1.21.3".split("\\.");

        for (int i = 0; i < Math.max(c.length, r.length); i++) {
            int cv = (i < c.length) ? Integer.parseInt(c[i]) : 0;
            int rv = (i < r.length) ? Integer.parseInt(r[i]) : 0;

            if (cv > rv) return true;
            if (cv < rv) return false;
        }
        return true;
    }

    /**
     * 根据输入的字符串来匹配和返回对应的列表
     * @param input 输入
     * @param raw 需要匹配的列表
     * @return 返回的匹配列表
     */
    public static Set<String> tabList(String input, Set<String> raw) {
        if (input == null) input = "";

        String lowerInput = input.toLowerCase();
        return raw.stream()
                .filter(s -> s.toLowerCase().startsWith(lowerInput))
                .collect(Collectors.toSet());
    }

    /**
     * 将字符串列表转换为枚举列表的通用方法
     *
     * @param <T>         枚举类型，必须是Enum的子类
     * @param stringList  待转换的字符串列表
     * @param enumClass   目标枚举类的Class对象
     * @param caseSensitive true 小写，false 大写
     * @return 转换成功的枚举值列表，忽略无法转换的项
     */
    public static  <T extends Enum<T>> List<T> convertStringListToEnumList(List<String> stringList, Class<T> enumClass, boolean caseSensitive) {
        List<T> result = new ArrayList<>();
        for (String item : stringList) {
            if (item == null || item.trim().isEmpty()) {
                continue;
            }
            String cleanName = item.trim();
            try {
                String enumName = caseSensitive ? cleanName.toLowerCase() : cleanName.toUpperCase();
                T enumValue = Enum.valueOf(enumClass, enumName);
                result.add(enumValue);
                Log.debug("Successfully converted {} to enum.", cleanName);
            } catch (IllegalArgumentException e) {
                Log.error("Invalid enum value: {} for enum class {}", cleanName, enumClass.getSimpleName());
            }
        }
        return result;
    }

    /**
     * 根据输入参数解析 UUID
     * @param value 玩家名字或 UUID
     * @return 玩家 UUID，如果不存在则返回 null
     */
    public static UUID parseUUID(String value) {
        AtomicReference<UUID> uuid = new AtomicReference<>(null);
        try {
            uuid.set(UUID.fromString(value));
        } catch (Exception ignored) {
        }
        if (uuid.get() == null) {
            try {
                uuid.set(Bukkit.getOfflinePlayer(value).getUniqueId());
            } catch (Exception e) {
                return null;
            }
        }
        return uuid.get();
    }

}
