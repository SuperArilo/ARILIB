package com.tty.lib.services.impl.interact;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.tty.api.service.InteractService;
import com.tty.lib.Lib;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardInteract implements InteractService {

    private final RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();

    @Override
    public String pluginName() {
        return "WorldGuard";
    }

    @Override
    public boolean canBuild(Location location) {
        return query.testState(BukkitAdapter.adapt(location), null, Flags.BUILD);
    }

    @Override
    public boolean canBuild(Location location, Player player) {
        return query.testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
    }

    @Override
    public boolean canTeleport(Location location) {
        boolean status = true;
        ApplicableRegionSet regions = query.getApplicableRegions(BukkitAdapter.adapt(location));
        for (ProtectedRegion region : regions) {
            if (region.getFlag(Flags.ENTRY) == StateFlag.State.DENY) {
                Lib.log.debug("location: x: {}, y: {}, z: {} in WorldGuard not allow.", location.getX(), location.getY(), location.getZ());
                status = false;
            }
        }
        return status;
    }

    @Override
    public boolean canTeleport(Location location, Player player) {
        return this.query.testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player), Flags.ENTRY);
    }

    @Override
    public boolean canInteract(Location location) {
        com.sk89q.worldedit.util.Location adapt = BukkitAdapter.adapt(location);
        ApplicableRegionSet regions = query.getApplicableRegions(adapt);
        if (regions.size() == 0) return true;
        return this.query.testState(adapt, null, Flags.BUILD);
    }

    @Override
    public boolean canInteract(Location location, Player player) {
        return this.query.testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
    }

}