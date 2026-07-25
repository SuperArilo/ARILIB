package com.tty.lib.services.impl;

import com.tty.api.service.AttackService;
import com.tty.lib.Lib;
import com.tty.lib.services.impl.pvp.PvpManagerInteract;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AttackServiceImpl implements AttackService {

    private final List<AttackService> services = new ArrayList<>();

    public AttackServiceImpl() {
        this.loadOtherPlugin(Lib.instance, "PvPManager", PvpManagerInteract.class, this.services::add);
    }

    @Override
    public boolean isInPvp(Player player) {
        for (AttackService service : this.services) {
            if (!service.isInPvp(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canAttackPlayer(Player damager, Player victim) {
        for (AttackService service : this.services) {
            if (!service.canAttackPlayer(damager, victim)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void changePlayerPvpStatus(Player player, boolean pvpStatus) {
        for (AttackService service : this.services) {
            service.changePlayerPvpStatus(player, pvpStatus);
        }
    }

    @Override
    public String pluginName() {
        return Lib.instance.getName();
    }

}
