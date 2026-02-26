package com.tty.lib;

import com.tty.api.*;
import com.tty.api.service.*;
import com.tty.api.utils.PublicFunctionUtils;
import com.tty.lib.commands.AriLib;
import com.tty.lib.enum_type.FilePath;
import com.tty.lib.listener.OnPluginReloadListener;
import com.tty.lib.services.impl.*;
import com.tty.lib.tool.Placeholder;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Lib extends JavaPlugin {

    public static Lib instance;
    public static final Log log = Log.create();
    public static Boolean DEBUG = false;
    public static final Scheduler SCHEDULER = Scheduler.create();
    public static ConfigInstance C_INSTANCE;
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
        log.setDebug(DEBUG);
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
        CONFIG_DATA_SERVICE = new ConfigDataServiceImpl();
        NBT_DATA_SERVICE = new NBTDataServiceImpl();
        FIREWORK_SERVICE = new FireworkServiceImpl();
        TELEPORTING_SERVICE = new TeleportingServiceImpl();
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
        C_INSTANCE = new ConfigInstance(Lib.instance, FilePath.values());
    }
}
