package com.tty.lib.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.tty.lib.Log;
import com.tty.lib.enum_type.FilePathEnum;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tty.lib.tool.FormatUtils.copySectionToYamlConfiguration;
import static com.tty.lib.tool.FormatUtils.yamlConvertToObj;

public class ConfigInstance {

    protected final Map<String, YamlConfiguration> CONFIGS = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public <E extends Enum<E> & FilePathEnum> String getValue(String keyPath, E filePath) {
        return getValue(keyPath, filePath, String.class, "null");
    }


    public <T, E extends Enum<E> & FilePathEnum> T getValue(String keyPath, E filePath, Class<T> tClass) {
        if (checkPath(keyPath)) return null;
        YamlConfiguration configuration = checkConfiguration(filePath);
        if (configuration == null) return null;
        return configuration.getObject(keyPath, tClass);
    }

    public <T, E extends Enum<E> & FilePathEnum> T getValue(String keyPath, E filePath, Type type, T defaultValue) {
        if (checkPath(keyPath)) return defaultValue;

        YamlConfiguration fileConfiguration = checkConfiguration(filePath);
        if (fileConfiguration == null) return defaultValue;

        Object value = fileConfiguration.get(keyPath);
        if (value == null) {
            Log.warn("Value not found for path: {} in file: {}", keyPath, filePath.name());
            return defaultValue;
        }

        try {
            if (value instanceof MemorySection) {
                YamlConfiguration tempConfig = new YamlConfiguration();
                copySectionToYamlConfiguration((ConfigurationSection) value, tempConfig);
                return yamlConvertToObj(tempConfig.saveToString(), type);
            } else {
                return this.gson.fromJson(this.gson.toJsonTree(value), type);
            }
        } catch (JsonSyntaxException e) {
            Log.error(e, "Failed to convert value at path: {} in file: {} to type: {}", keyPath, filePath.name(), type.getTypeName());
            return defaultValue;
        }
    }

    public YamlConfiguration getObject(String fileName) {
        return CONFIGS.get(fileName);
    }

    private boolean checkPath(String path) {
        if (path == null || path.isEmpty()) {
            Log.error("file path {} is empty or null", path);
            return true;
        }
        return false;
    }

    public <T extends Enum<T> & FilePathEnum> void setValue(JavaPlugin plugin, String topKeyPath, T filePath, Map<String, Object> values) {
        YamlConfiguration configuration = this.checkConfiguration(filePath);
        if (configuration == null) throw new RuntimeException("Config file not found: " + filePath.name());

        values.forEach((k, v) -> configuration.set(topKeyPath + "." + k, v));
        setConfig(filePath.name(), configuration);

        File file = new File(plugin.getDataFolder(), filePath.getPath());
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Enum<T> & FilePathEnum> YamlConfiguration checkConfiguration(T filePath) {
        YamlConfiguration configuration = this.getObject(filePath.name());
        if (configuration == null) {
            Log.error("Config file not found: {}", filePath.name());
            return null;
        }
        return configuration;
    }

    public void setConfig(String name, YamlConfiguration instance) {
        CONFIGS.put(name, instance);
    }

    public void clearConfigs() {
        CONFIGS.clear();
    }
}
