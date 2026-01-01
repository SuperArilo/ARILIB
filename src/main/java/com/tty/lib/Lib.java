package com.tty.lib;

import com.tty.lib.command.AriLib;
import com.tty.lib.enum_type.FilePath;
import com.tty.lib.listener.OnPluginReloadListener;
import com.tty.lib.scheduler.BukkitScheduler;
import com.tty.lib.scheduler.FoliaScheduler;
import com.tty.lib.services.ConfigDataService;
import com.tty.lib.services.impl.ConfigDataServiceImpl;
import com.tty.lib.tool.ConfigInstance;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Lib extends JavaPlugin {

    public static Lib instance;
    public static Boolean DEBUG = false;
    public static final Scheduler Scheduler = ServerPlatform.isFolia() ? new FoliaScheduler():new BukkitScheduler();
    public static final ConfigInstance C_INSTANCE = new ConfigInstance();
    public static ConfigDataService CONFIG_DATA_SERVICE;

    @Override
    public void onLoad() {
        instance = this;
        reloadAllConfig();
        Log.init(this.getLogger(), DEBUG);

        this.registerServices();
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new OnPluginReloadListener(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, i -> {
            Commands registrar = i.registrar();
            registrar.register(new AriLib().toBrigadier());
        });
    }

    @Override
    public void onDisable() {
        C_INSTANCE.clearConfigs();
        Bukkit.getServicesManager().unregister(true);
    }

    private void registerServices () {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        CONFIG_DATA_SERVICE = new ConfigDataServiceImpl();
        servicesManager.register(ConfigDataService.class, CONFIG_DATA_SERVICE, this, ServicePriority.Normal);
    }

    public static void reloadAllConfig() {
        Lib.instance.saveDefaultConfig();
        Lib.instance.reloadConfig();
        DEBUG = Lib.instance.getConfig().getBoolean("debug.enable", false);
        C_INSTANCE.clearConfigs();
        FileConfiguration instanceConfig = Lib.instance.getConfig();
        for (FilePath filePath : FilePath.values()) {
            String path = filePath.getPath();
            if(filePath.equals(FilePath.Lang)) {
                path = path.replace("[lang]", instanceConfig.getString("lang", "cn"));
            }
            File file = new File(Lib.instance.getDataFolder(), path);
            if (!file.exists()) {
                Lib.instance.saveResource(path, true);
            } else if (instanceConfig.getBoolean("debug.overwrite-file", false)) {
                try {
                    Lib.instance.saveResource(path, true);
                } catch (Exception e) {
                    Log.error("can not find file path %s .", path);
                }
            }
            C_INSTANCE.setConfig(filePath.name(), YamlConfiguration.loadConfiguration(file));
        }
    }
}
