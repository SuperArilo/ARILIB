package com.tty.lib.services.impl;

import com.tty.api.service.InteractService;
import com.tty.lib.Lib;
import com.tty.lib.services.impl.interact.DominionInteract;
import com.tty.lib.services.impl.interact.ResidenceInteract;
import com.tty.lib.services.impl.interact.WorldGuardInteract;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InteractServiceImpl implements InteractService {

    private final List<InteractService> delegates = new ArrayList<>();

    public InteractServiceImpl() {
        this.loadOtherPlugin(Lib.instance, "WorldGuard", WorldGuardInteract.class, this.delegates::add);
        this.loadOtherPlugin(Lib.instance, "Residence", ResidenceInteract.class, this.delegates::add);
        this.loadOtherPlugin(Lib.instance, "Dominion", DominionInteract.class, this.delegates::add);
    }

    @Override
    public String pluginName() {
        return Lib.instance.getName();
    }

    @Override
    public boolean canBuild(Location location) {
        for (InteractService hook : this.delegates) {
            if (!hook.canBuild(location)) {
                Lib.instance.getLog().debug("{}: can not to build.", hook.pluginName());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canBuild(Location location, Player player) {
        for (InteractService hook : this.delegates) {
            if (!hook.canBuild(location, player)) {
                Lib.instance.getLog().debug("{}: can not to build.", hook.pluginName());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canTeleport(Location location) {
        for (InteractService hook : this.delegates) {
            if (!hook.canTeleport(location)) {
                Lib.instance.getLog().debug("{}: can not to teleport.", hook.pluginName());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canTeleport(Location location, Player player) {
        for (InteractService hook : this.delegates) {
            if (!hook.canTeleport(location, player)) {
                Lib.instance.getLog().debug("{}: can not to teleport.", hook.pluginName());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canInteract(Location location) {
        for (InteractService hook : this.delegates) {
            if (!hook.canInteract(location)) {
                Lib.instance.getLog().debug("{}: can not to interact.", hook.pluginName());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canInteract(Location location, Player player) {
        for (InteractService hook : this.delegates) {
            if (!hook.canInteract(location, player)) {
                Lib.instance.getLog().debug("{}: can not to interact.", hook.pluginName());
                return false;
            }
        }
        return true;
    }

}