package com.tty.lib.services.impl.interact;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.tty.api.service.InteractService;
import com.tty.lib.Lib;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ResidenceInteract implements InteractService {

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
        ClaimedResidence byLoc = Residence.getInstance().getResidenceManager().getByLoc(location);
        status = byLoc == null;
        if (!status) {
            Lib.log.debug("location: x: {}, y: {}, z: {} in Residence not allow.", location.getX(), location.getY(), location.getZ());
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
