package com.tty.lib.services.impl;

import com.tty.api.service.InteractService;
import com.tty.lib.Lib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InteractServiceImpl implements InteractService {

    private final List<InteractService> delegates = new ArrayList<>();

    public InteractServiceImpl() {
        this.loadHooks();
    }

    private void loadHooks() {
        load("WorldGuard", "WorldGuardInteract");
        load("Residence", "ResidenceInteract");
        load("Dominion", "DominionInteract");
    }

    private void load(String plugin, String clazzName) {
        if (!Bukkit.getPluginManager().isPluginEnabled(plugin)) return;
        try {
            Class<?> clazz = Class.forName("com.tty.lib.services.impl.interact." + clazzName);
            InteractService hook = (InteractService) clazz.getDeclaredConstructor().newInstance();
            this.delegates.add(hook);
        } catch (Throwable e) {
            Lib.log.warn("failed to load hook: " + clazzName);
        }
    }

    @Override
    public boolean canBuild(Location location) {
        for (InteractService hook : this.delegates) {
            if (!hook.canBuild(location)) return false;
        }
        return true;
    }

    @Override
    public boolean canBuild(Location location, Player player) {
        for (InteractService hook : this.delegates) {
            if (!hook.canBuild(location, player)) return false;
        }
        return true;
    }

    @Override
    public boolean canTeleport(Location location) {
        for (InteractService hook : this.delegates) {
            if (!hook.canTeleport(location)) return false;
        }
        return true;
    }

    @Override
    public boolean canTeleport(Location location, Player player) {
        for (InteractService hook : this.delegates) {
            if (!hook.canTeleport(location, player)) return false;
        }
        return true;
    }

    @Override
    public boolean canInteract(Location location) {
        for (InteractService hook : this.delegates) {
            if (!hook.canInteract(location)) return false;
        }
        return true;
    }

    @Override
    public boolean canInteract(Location location, Player player) {
        for (InteractService hook : this.delegates) {
            if (!hook.canInteract(location, player)) return false;
        }
        return true;
    }
}