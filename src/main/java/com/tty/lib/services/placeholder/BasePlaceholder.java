package com.tty.lib.services.placeholder;

import com.google.common.reflect.TypeToken;
import com.tty.lib.enum_type.FilePathEnum;
import com.tty.lib.services.impl.PlaceholderEngineImpl;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.ConfigInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BasePlaceholder<E extends Enum<E> & FilePathEnum> {

    private final PlaceholderEngineImpl engine = new PlaceholderEngineImpl();
    private final ConfigInstance instance;
    private final E type;

    private final Type typeTokenList = new TypeToken<List<String>>() {}.getType();

    public BasePlaceholder(ConfigInstance instance, E type) {
        this.instance = instance;
        this.type = type;
    }

    protected Component empty() {
        return Component.empty();
    }

    protected CompletableFuture<Component> emptyAsync() {
        return CompletableFuture.completedFuture(Component.empty());
    }

    protected CompletableFuture<Component> setAsync(String value) {
        CompletableFuture<Component> future = new CompletableFuture<>();
        future.complete(ComponentUtils.text(value));
        return future;
    }

    protected Component set(String value) {
        return ComponentUtils.text(value);
    }

    public CompletableFuture<Component> renderAsync(String path, Player player) {
        return this.engine.renderAsync(instance.getValue(path, type, String.class, "null"), player);
    }

    public CompletableFuture<Component> renderAsync(String path, OfflinePlayer offlinePlayer) {
        return this.engine.renderAsync(instance.getValue(path, type, String.class, "null"), offlinePlayer);
    }

    public CompletableFuture<Component> renderListAsync(String path, Player player) {
        return this.engine.renderListAsync(instance.getValue(path, type, typeTokenList, List.of()), player);
    }

    public CompletableFuture<Component> renderListAsync(String path, OfflinePlayer offlinePlayer) {
        return this.engine.renderListAsync(instance.getValue(path, type, typeTokenList, List.of()), offlinePlayer);
    }

    public Component renderSync(String path, Player player) {
        return this.engine.render(instance.getValue(path, type, String.class, "null"), player);
    }

    public Component renderSync(String path, OfflinePlayer offlinePlayer) {
        return this.engine.render(instance.getValue(path, type, String.class, "null"), offlinePlayer);
    }

    public Component renderListSync(String path, Player player) {
        return this.engine.renderList(instance.getValue(path, type, typeTokenList, List.of()), player);
    }

    public Component renderListSync(String path, OfflinePlayer offlinePlayer) {
        return this.engine.renderList(instance.getValue(path, type, typeTokenList, List.of()), offlinePlayer);
    }

    protected void setSyncRegister(PlaceholderRegistry<Component> registry) {
        this.engine.setRegistrySync(registry);
    }

    protected void setAsyncRegister(PlaceholderRegistry<CompletableFuture<Component>> registry) {
        this.engine.setRegistryAsync(registry);
    }

}
