package com.tty.lib.services.impl.interact;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import com.tty.api.service.InteractService;
import com.tty.lib.Lib;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DominionInteract implements InteractService {

    @Override
    public boolean canBuild(Location location) {
        return false;
    }

    @Override
    public boolean canBuild(Location location, Player player) {
        return false;
    }

    @Override
    public boolean canTeleport(Location location) {
        boolean status;
        DominionDTO dominion = DominionAPI.getInstance().getDominion(location);
        status = dominion == null;
        if (!status) {
            Lib.log.debug("location: x: {}, y: {}, z: {} in Dominion not allow.", location.getX(), location.getY(), location.getZ());
        }
        return status;
    }

    @Override
    public boolean canTeleport(Location location, Player player) {
        return false;
    }

    @Override
    public boolean canInteract(Location location) {
        return false;
    }

    @Override
    public boolean canInteract(Location location, Player player) {
        return false;
    }

}
