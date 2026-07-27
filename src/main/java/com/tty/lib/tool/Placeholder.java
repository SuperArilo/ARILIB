package com.tty.lib.tool;

import com.tty.api.service.impl.PlaceholderRegistryImpl;
import com.tty.api.service.placeholder.PlaceholderDefinition;
import com.tty.api.service.placeholder.PlaceholderRegistry;
import com.tty.api.service.placeholder.PlaceholderResolve;
import com.tty.lib.Lib;
import com.tty.lib.enumType.lang.PlaceholderServer;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class Placeholder {

    private static final Properties PLUGIN_INFO = new Properties();
    
    static {
        try (InputStream inputStream = Lib.instance.getClass().getClassLoader().getResourceAsStream("git.properties")) {
            if (inputStream == null) {
                Lib.instance.getLog().debug("could not found file git.properties in jar.");
            } else {
                PLUGIN_INFO.load(inputStream);
            }
        } catch (IOException e) {
            Lib.instance.getLog().debug(e, "could not found file git.properties in jar.");
        }
    }
    
    public static PlaceholderRegistry register() {
        PlaceholderRegistry registry = new PlaceholderRegistryImpl();
        
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.SERVER_VERSION,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(Bukkit.getName() + " " + Bukkit.getServer().getVersion())))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_NAME,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(Lib.instance.getName())))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_BRANCH,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(PLUGIN_INFO.getProperty("git.branch"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_BUILD_TIME,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(OffsetDateTime.parse(PLUGIN_INFO.getProperty("git.build.time")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_BUILD_VERSION,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(PLUGIN_INFO.getProperty("git.build.version") + "-" + PLUGIN_INFO.getProperty("git.commit.id.abbrev"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_COMMIT_ID_ABBREV,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(PLUGIN_INFO.getProperty("git.commit.id.abbrev"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_COMMIT_MESSAGE,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(PLUGIN_INFO.getProperty("git.commit.message.full"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_COMMIT_TIME,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(OffsetDateTime.parse(PLUGIN_INFO.getProperty("git.commit.time")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_COMMIT_USER_NAME,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(PLUGIN_INFO.getProperty("git.commit.user.name"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_GIT_TAG,
                PlaceholderResolve.ofWhenNull((() -> CompletableFuture.completedFuture(PLUGIN_INFO.getProperty("git.tag"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_DEBUG_STATUS,
                PlaceholderResolve.ofWhenNull(()-> CompletableFuture.completedFuture(String.valueOf(Lib.instance.isDebug()))))
        );

        return registry;
    }

}
