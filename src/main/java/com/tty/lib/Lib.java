package com.tty.lib;

import com.tty.api.BaseJavaPlugin;
import com.tty.api.dto.TempRegisterService;
import com.tty.api.enumType.FilePathEnum;
import com.tty.api.service.*;
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
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    protected void loading() {
        instance = this;
        PLACEHOLDER = new Placeholder();
    }

    @Override
    protected void enabling() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, i -> {
            Commands registrar = i.registrar();
            registrar.register(new AriLib().toBrigadier());
        });

        this.registerService();
    }

    @Override
    protected void disabling() {
        Bukkit.getServicesManager().unregister(true);
    }

    @Override
    protected List<TempRegisterService<?>> loadOtherPlugin() {
        return List.of(
                TempRegisterService.of("Vault", Economy.class, i -> ECONOMY_SERVICE = new EconomyServiceImpl(i)),
                TempRegisterService.of("Vault", Permission.class, i -> PERMISSION_SERVICE = new PermissionServiceImpl(i))
        );
    }

    @Override
    protected @NotNull List<Listener> registerEvents() {
        return List.of(new OnPluginReloadListener());
    }

    @Override
    protected @NotNull FilePathEnum @NotNull [] fileList() {
        return FilePath.values();
    }

    private void registerService () {
        ServicesManager servicesManager = Bukkit.getServicesManager();
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
