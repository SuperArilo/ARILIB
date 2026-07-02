package com.tty.lib.services.impl;

import com.tty.api.service.EconomyService;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public record EconomyServiceImpl(Economy economy) implements EconomyService {

    @Override
    public EconomyResponse depositPlayer(Player player, double cost) {
        if (!this.isNull()) return this.economy.depositPlayer(player, cost);
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(Player player, double cost) {
        if(!this.isNull()) return this.economy.withdrawPlayer(player, cost);
        return null;
    }

    @Override
    public Double getBalance(Player player) {
        if (!this.isNull()) return this.economy.getBalance(player);
        return 0.0;
    }

    @Override
    public boolean hasEnoughBalance(Player player, double cost) {
        if(this.isNull()) return true;
        return this.getBalance(player) >= cost;
    }

    @Override
    public String getNamePlural() {
        return this.economy.currencyNamePlural();
    }

    @Override
    public String getNameSingular() {
        return this.economy.currencyNameSingular();
    }

    @Override
    public boolean isNull() {
        return this.economy == null;
    }

}
