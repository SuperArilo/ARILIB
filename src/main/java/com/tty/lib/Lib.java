package com.tty.lib;

import com.tty.api.Log;
import com.tty.api.PublicFunctionUtils;
import com.tty.api.Scheduler;
import com.tty.api.service.ComponentService;
import com.tty.lib.commands.AriLib;
import com.tty.lib.enum_type.FilePath;
import com.tty.lib.listener.OnPluginReloadListener;
import com.tty.lib.services.*;
import com.tty.lib.services.impl.*;
import com.tty.api.ConfigInstance;
import com.tty.lib.tool.Placeholder;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
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
    public static final Scheduler SCHEDULER = Scheduler.create();
    public static final ConfigInstance C_INSTANCE = new ConfigInstance();
    public static ComponentService COMPONENT_SERVICE;
    public static EconomyService ECONOMY_SERVICE;
    public static PermissionService PERMISSION_SERVICE;
    public static ConfigDataService CONFIG_DATA_SERVICE;
    public static NBTDataService NBT_DATA_SERVICE;
    public static FireworkService FIREWORK_SERVICE;
    public static TeleportingService TELEPORTING_SERVICE;
    public static final Placeholder PLACEHOLDER = new Placeholder();

    @Override
    public void onLoad() {
        instance = this;
        reloadAllConfig();
        Log.init(this.getLogger(), DEBUG);
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new OnPluginReloadListener(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, i -> {
            Commands registrar = i.registrar();
            registrar.register(new AriLib().toBrigadier());
        });

        this.registerServices();
    }

    @Override
    public void onDisable() {
        C_INSTANCE.clearConfigs();
        Bukkit.getServicesManager().unregister(true);
    }

    private void registerServices () {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        PublicFunctionUtils.loadPlugin("Vault", Economy.class, i -> ECONOMY_SERVICE = new EconomyServiceImpl(i));
        PublicFunctionUtils.loadPlugin("Vault", Permission.class, i -> PERMISSION_SERVICE = new PermissionServiceImpl(i));
        COMPONENT_SERVICE = new ComponentServiceImpl();
        CONFIG_DATA_SERVICE = new ConfigDataServiceImpl();
        NBT_DATA_SERVICE = new NBTDataServiceImpl();
        FIREWORK_SERVICE = new FireworkServiceImpl();
        TELEPORTING_SERVICE = new TeleportingServiceImpl();
        servicesManager.register(ComponentService.class, COMPONENT_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(EconomyService.class, ECONOMY_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(PermissionService.class, PERMISSION_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(ConfigDataService.class, CONFIG_DATA_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(NBTDataService.class, NBT_DATA_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(FireworkService.class, FIREWORK_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(TeleportingService.class, TELEPORTING_SERVICE, this, ServicePriority.Normal);
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
                    Log.error("can not find file path {} .", path);
                }
            }
            C_INSTANCE.setConfig(filePath.name(), YamlConfiguration.loadConfiguration(file));
        }
    }
}
