package com.tty.lib.services.impl;

import com.tty.lib.services.EconomyService;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public record EconomyServiceImpl(Economy economy) implements EconomyService {

    @Override
    public EconomyResponse depositPlayer(Player player, double cost) {
        if (!isNull()) return economy.depositPlayer(player, cost);
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(Player player, double cost) {
        if(!isNull()) return economy.withdrawPlayer(player, cost);
        return null;
    }

    @Override
    public Double getBalance(Player player) {
        if (!isNull()) return economy.getBalance(player);
        return 0.0;
    }

    @Override
    public boolean hasEnoughBalance(Player player, double cost) {
        if(isNull()) return true;
        return getBalance(player) >= cost;
    }

    @Override
    public String getNamePlural() {
        return economy.currencyNamePlural();
    }

    @Override
    public String getNameSingular() {
        return economy.currencyNameSingular();
    }

    @Override
    public boolean isNull() {
        return economy == null;
    }

}
