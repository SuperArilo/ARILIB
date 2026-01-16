package com.tty.lib.services.placeholder;

import com.google.common.reflect.TypeToken;
import com.tty.lib.enum_type.FilePathEnum;
import com.tty.lib.services.impl.PlaceholderEngineImpl;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.ConfigInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BasePlaceholder<E extends Enum<E> & FilePathEnum> {

    private final PlaceholderEngineImpl engine = new PlaceholderEngineImpl();
    private final ConfigInstance instance;
    private final E type;

    public BasePlaceholder(ConfigInstance instance, E type) {
        this.instance = instance;
        this.type = type;
    }

    protected CompletableFuture<Component> empty() {
        return CompletableFuture.completedFuture(Component.empty());
    }

    protected CompletableFuture<Component> set(String value) {
        CompletableFuture<Component> future = new CompletableFuture<>();
        future.complete(ComponentUtils.text(value));
        return future;
    }

    public CompletableFuture<Component> render(String path, Player player) {
        String value = instance.getValue(path, type, String.class, "null");
        return this.engine.render(value, player);
    }

    public CompletableFuture<Component> render(String path, OfflinePlayer offlinePlayer) {
        String value = instance.getValue(path, type, String.class, "null");
        return this.engine.render(value, offlinePlayer);
    }

    public CompletableFuture<Component> renderList(String path, Player player) {
        return this.engine.renderList(instance.getValue(path, type, new TypeToken<List<String>>(){}.getType(), List.of()), player);
    }

    public CompletableFuture<Component> renderList(String path, OfflinePlayer offlinePlayer) {
        return this.engine.renderList(instance.getValue(path, type, new TypeToken<List<String>>(){}.getType(), List.of()), offlinePlayer);
    }

    protected void setRegister(PlaceholderRegistry registry) {
        this.engine.setRegistry(registry);
    }
}
