package com.tty.lib.gui;

import com.tty.lib.Log;
import com.tty.lib.enum_type.GuiType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class BaseInventory implements InventoryHolder {

    public final JavaPlugin plugin;
    public final Player player;
    protected Inventory inventory;
    public final GuiType type;

    public BaseInventory(JavaPlugin plugin, Player player, GuiType type) {
        this.plugin = plugin;
        this.player = player;
        this.type = type;
    }

    public void open() {
        this.inventory = this.createInventory();
        this.beforeOpen();
        if (this.inventory == null) {
            Log.error("player {} open inventory error. because inventory is null.");
            return;
        }
        this.player.openInventory(this.inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    protected abstract void beforeOpen();

    protected abstract Inventory createInventory();

    public abstract void clean();

    public void cleanup() {
        if (this.inventory != null) {
            this.inventory.clear();
        }
        this.inventory = null;
        this.clean();
    }

}
