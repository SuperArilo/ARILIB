package com.tty.lib.tool;

import com.tty.api.AbstractJavaPlugin;
import com.tty.api.service.impl.PlaceholderRegistryImpl;
import com.tty.api.service.placeholder.BasePlaceholder;
import com.tty.api.service.placeholder.PlaceholderDefinition;
import com.tty.api.service.placeholder.PlaceholderRegistry;
import com.tty.api.service.placeholder.PlaceholderResolve;
import com.tty.lib.Lib;
import com.tty.lib.configuration.lang.LangConfig;
import com.tty.lib.enumType.lang.PlaceholderServer;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;


public class Placeholder extends BasePlaceholder {

    private final Properties pluginInfo = new Properties();

    public Placeholder(AbstractJavaPlugin plugin) {
        super(plugin, Lib.instance.getConfigurationManager().get(LangConfig.class));
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("git.properties")) {
            if (inputStream == null) {
                Lib.instance.getLog().debug("could not found file git.properties in jar.");
                return;
            }
            this.pluginInfo.load(inputStream);
        } catch (IOException e) {
            Lib.instance.getLog().debug(e, "could not found file git.properties in jar.");
        }
        this.init();
    }

    public void init() {
        PlaceholderRegistryImpl registry = new PlaceholderRegistryImpl();
        this.register(registry);
        this.addRegister(registry);
    }

    private void register(PlaceholderRegistry registry) {
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.SERVER_VERSION,
                PlaceholderResolve.ofWhenNull((() -> this.set(Bukkit.getName() + " " + Bukkit.getServer().getVersion())))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_NAME,
                PlaceholderResolve.ofWhenNull((() -> this.set(Lib.instance.getName())))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_BRANCH,
                PlaceholderResolve.ofWhenNull((() -> this.set(this.pluginInfo.getProperty("git.branch"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_BUILD_TIME,
                PlaceholderResolve.ofWhenNull((() -> this.set(OffsetDateTime.parse(this.pluginInfo.getProperty("git.build.time")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_BUILD_VERSION,
                PlaceholderResolve.ofWhenNull((() -> this.set(this.pluginInfo.getProperty("git.build.version") + "-" + this.pluginInfo.getProperty("git.commit.id.abbrev"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_COMMIT_ID_ABBREV,
                PlaceholderResolve.ofWhenNull((() -> this.set(this.pluginInfo.getProperty("git.commit.id.abbrev"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_COMMIT_MESSAGE,
                PlaceholderResolve.ofWhenNull((() -> this.set(this.pluginInfo.getProperty("git.commit.message.full"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_COMMIT_TIME,
                PlaceholderResolve.ofWhenNull((() -> this.set(OffsetDateTime.parse(this.pluginInfo.getProperty("git.commit.time")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_COMMIT_USER_NAME,
                PlaceholderResolve.ofWhenNull((() -> this.set(this.pluginInfo.getProperty("git.commit.user.name"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_GIT_TAG,
                PlaceholderResolve.ofWhenNull((() -> this.set(this.pluginInfo.getProperty("git.tag"))))
        ));
        registry.register(PlaceholderDefinition.of(
                PlaceholderServer.PLUGIN_DEBUG_STATUS,
                PlaceholderResolve.ofWhenNull(()-> this.set(String.valueOf(Lib.instance.isDebug()))))
        );
    }

}
