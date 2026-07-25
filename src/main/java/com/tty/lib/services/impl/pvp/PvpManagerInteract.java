package com.tty.lib.services.impl.pvp;

import com.tty.api.service.AttackService;
import me.chancesd.pvpmanager.PvPManager;
import me.chancesd.pvpmanager.player.CombatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PvpManagerInteract implements AttackService {

    private final PvPManager manager;

    public PvpManagerInteract() {
        this.manager = (PvPManager) Bukkit.getServer().getPluginManager().getPlugin("PvPManager");
    }

    @Override
    public String pluginName() {
        return "PvPManager";
    }

    @Override
    public boolean isInPvp(Player player) {
        CombatPlayer combatPlayer = CombatPlayer.get(player);
        if (combatPlayer == null) return false;
        return combatPlayer.isInCombat();
    }

    @Override
    public boolean canAttackPlayer(Player damager, Player victim) {
        CombatPlayer combatDamager = CombatPlayer.get(damager);
        CombatPlayer combatVictim = CombatPlayer.get(victim);
        if (combatDamager == null || combatVictim == null) return false;
        if (combatVictim.hasRespawnProtection()) return false;
        return this.manager.getPlayerManager().canAttack(damager, victim);
    }

    @Override
    public void changePlayerPvpStatus(Player player, boolean pvpStatus) {
        CombatPlayer combatPlayer = CombatPlayer.get(player);
        if (combatPlayer != null) {
            combatPlayer.setPvP(pvpStatus);
        }
    }

}
