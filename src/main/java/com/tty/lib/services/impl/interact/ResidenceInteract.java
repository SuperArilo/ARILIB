package com.tty.lib.services.impl.interact;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.tty.api.service.InteractService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ResidenceInteract implements InteractService {

    @Override
    public String pluginName() {
        return Residence.getInstance().getName();
    }

    @Override
    public boolean canBuild(Location location) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(location);
        if (res == null) return true;
        return res.getPermissions().has(Flags.build, false);
    }

    @Override
    public boolean canBuild(Location location, Player player) {
        ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(location);
        if (residence == null) return true;
        return residence.getPermissions().playerHas(player, location.getWorld().getName(), Flags.build, false);
    }

    @Override
    public boolean canTeleport(Location location) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(location);
        if (res == null) return true;
        return res.getPermissions().has(Flags.tp, false);
    }

    @Override
    public boolean canTeleport(Location location, Player player) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(location);
        if (res == null) return true;
        return res.getPermissions().playerHas(player, Flags.tp, false);
    }

    @Override
    public boolean canInteract(Location location) {
        ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(location);
        if (residence == null) return true;
        return residence.getPermissions().has(Flags.use, false);
    }

    @Override
    public boolean canInteract(Location location, Player player) {
        ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(location);
        if (residence == null) return true;
        return residence.getPermissions().playerHas(player, Flags.use, false);
    }
}