package com.tty.lib.services.impl.pvp;

import com.tty.api.service.AttackService;
import com.tty.lib.Lib;
import me.chancesd.pvpmanager.PvPManager;
import me.chancesd.pvpmanager.event.PlayerTagEvent;
import me.chancesd.pvpmanager.player.CombatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PvpManagerInteract implements AttackService, Listener {

    private final PvPManager manager;
    private final List<Player> needToCancelAttackStatus = new CopyOnWriteArrayList<>();

    public PvpManagerInteract() {
        this.manager = (PvPManager) Bukkit.getServer().getPluginManager().getPlugin("PvPManager");
        Bukkit.getServer().getPluginManager().registerEvents(this, Lib.instance);
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
    public void changePvpStatus(Player player, boolean pvpStatus) {
        CombatPlayer combatPlayer = CombatPlayer.get(player);
        if (combatPlayer == null) return;
        combatPlayer.setPvP(pvpStatus);
    }

    @Override
    public void cancelPvpTag(@NotNull Player player) {
        this.needToCancelAttackStatus.add(player);
    }

    @EventHandler
    public void onTag(PlayerTagEvent event) {
        if (this.needToCancelAttackStatus.remove(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

}
