package com.tty.lib;

import com.tty.api.*;
import com.tty.api.enumType.FilePathEnum;
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

public class Lib extends BaseJavaPlugin {

    public static Lib instance;
    public static EconomyService ECONOMY_SERVICE;
    public static PermissionService PERMISSION_SERVICE;
    public static ConfigDataService CONFIG_DATA_SERVICE;
    public static NBTDataService NBT_DATA_SERVICE;
    public static FireworkService FIREWORK_SERVICE;
    public static TeleportingService TELEPORTING_SERVICE;
    public static InteractService INTERACT_SERVICE;
    public static Placeholder PLACEHOLDER;

    @Override
    public void onLoad() {
        super.onLoad();
        instance = this;
        PLACEHOLDER = new Placeholder();
    }

    @Override
    public void onEnable() {
        super.onEnable();
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
        super.onDisable();
        Bukkit.getServicesManager().unregister(true);
    }

    @Override
    protected FilePathEnum[] fileList() {
        return FilePath.values();
    }

    private void registerServices () {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        PublicFunctionUtils.loadPlugin("Vault", Economy.class, i -> ECONOMY_SERVICE = new EconomyServiceImpl(i));
        PublicFunctionUtils.loadPlugin("Vault", Permission.class, i -> PERMISSION_SERVICE = new PermissionServiceImpl(i));
        CONFIG_DATA_SERVICE = new ConfigDataServiceImpl();
        NBT_DATA_SERVICE = new NBTDataServiceImpl();
        FIREWORK_SERVICE = new FireworkServiceImpl();
        TELEPORTING_SERVICE = new TeleportingServiceImpl();
        INTERACT_SERVICE = new InteractServiceImpl();
        servicesManager.register(EconomyService.class, ECONOMY_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(PermissionService.class, PERMISSION_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(ConfigDataService.class, CONFIG_DATA_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(NBTDataService.class, NBT_DATA_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(FireworkService.class, FIREWORK_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(TeleportingService.class, TELEPORTING_SERVICE, this, ServicePriority.Normal);
        servicesManager.register(InteractService.class, INTERACT_SERVICE, this, ServicePriority.Normal);
    }

}
