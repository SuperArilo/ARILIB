package com.tty.lib.services.impl.interact;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import com.tty.api.service.InteractService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DominionInteract implements InteractService {

    @Override
    public String pluginName() {
        return "Dominion";
    }

    @Override
    public boolean canBuild(Location location) {
        DominionDTO dominion = DominionAPI.getInstance().getDominion(location);
        if (dominion == null) return true;
        return dominion.getGuestFlagValue(Flags.BREAK_BLOCK) && dominion.getGuestFlagValue(Flags.PLACE);
    }

    @Override
    public boolean canBuild(Location location, Player player) {
        DominionAPI instance = DominionAPI.getInstance();
        return instance.checkPrivilegeFlagSilence(location, Flags.BREAK_BLOCK, player) &&
                instance.checkPrivilegeFlagSilence(location, Flags.PLACE, player);
    }

    @Override
    public boolean canTeleport(Location location) {
        DominionDTO dominion = DominionAPI.getInstance().getDominion(location);
        if (dominion == null) return true;
        return dominion.getGuestFlagValue(Flags.TELEPORT);
    }

    @Override
    public boolean canTeleport(Location location, Player player) {
        return DominionAPI.getInstance().checkPrivilegeFlagSilence(location, Flags.TELEPORT, player);
    }

    @Override
    public boolean canInteract(Location location) {
        return DominionAPI.getInstance().getDominion(location) == null;
    }

    @Override
    public boolean canInteract(Location location, Player player) {
        DominionAPI instance = DominionAPI.getInstance();
        return instance.checkPrivilegeFlagSilence(location, Flags.CONTAINER, player) ||
                instance.checkPrivilegeFlagSilence(location, Flags.DOOR, player) ||
                instance.checkPrivilegeFlagSilence(location, Flags.LEVER, player) ||
                instance.checkPrivilegeFlagSilence(location, Flags.BUTTON, player);
    }

}