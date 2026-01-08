package com.tty.lib.gui;

import com.tty.lib.entity.gui.BaseMenu;
import com.tty.lib.entity.gui.FunctionItems;
import com.tty.lib.entity.gui.Mask;
import com.tty.lib.enum_type.GuiType;
import com.tty.lib.Log;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.tool.ComponentUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class BaseConfigInventory extends BaseInventory {

    public BaseMenu baseInstance;
    private final NamespacedKey renderType;

    public BaseConfigInventory(JavaPlugin plugin, BaseMenu instance, Player player, GuiType type) {
        super(plugin, player, type);
        this.renderType = new NamespacedKey(plugin, "type");
        this.baseInstance = instance;
    }

    @Override
    protected Inventory create() {
        return Bukkit.createInventory(this, this.baseInstance.getRow() * 9, ComponentUtils.text(this.baseInstance.getTitle(), player));
    }

    public void open() {
        super.open();
        this.renderMasks();
        this.renderFunctionItems();
    }

    protected abstract Mask renderCustomMasks();

    protected abstract Map<String, FunctionItems> renderCustomFunctionItems();

    private void renderMasks() {
        long l = System.currentTimeMillis();
        Mask mask = this.renderCustomMasks();
        if (mask == null) {
            mask = this.baseInstance.getMask();
        }
        List<TextComponent> collect = mask.getLore().stream().map(i -> ComponentUtils.text(i, this.player)).toList();
        for (Integer i : mask.getSlot()) {
            ItemStack itemStack = ItemStack.of(Material.valueOf(mask.getMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(ComponentUtils.text(mask.getName(), this.player));
            itemMeta.getPersistentDataContainer().set(this.renderType, PersistentDataType.STRING, FunctionType.MASK_ICON.name());
            itemMeta.lore(collect);
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(i, itemStack);
        }
        Log.debug("%s: render masks: %sms", this.type.name(), (System.currentTimeMillis() - l));
    }

    private void renderFunctionItems() {
        long l = System.currentTimeMillis();
        Map<String, FunctionItems> functionItems = this.renderCustomFunctionItems();
        if (functionItems == null || functionItems.isEmpty()) {
            functionItems = this.baseInstance.getFunctionItems();
        }
        functionItems.forEach((k, v) -> {
            ItemStack o = ItemStack.of(Material.valueOf(v.getMaterial().toUpperCase()));
            ItemMeta mo = o.getItemMeta();
            mo.displayName(ComponentUtils.text(v.getName(), this.player));
            mo.lore(v.getLore().stream().map(i -> ComponentUtils.text(i, this.player)).toList());
            mo.getPersistentDataContainer().set(this.renderType, PersistentDataType.STRING, v.getType().name());
            o.setItemMeta(mo);
            for (Integer integer : v.getSlot()) {
                this.inventory.setItem(integer, o);
            }
        });
        Log.debug("%s: render function items: %sms", this.type.name(), (System.currentTimeMillis() - l));
    }

    protected String replaceKey(String content, Map<String, String> map) {
        if (content == null || map == null || map.isEmpty()) {
            return content;
        }
        String result = content;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                result = result.replace("<" + entry.getKey() + ">" , entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.onCleanup();
    }

    protected void onCleanup() {
        this.baseInstance = null;
    }

    /**
     * 给指定的 ItemStack 的 ItemMeta 设置 NBT
     * @param itemMeta ItemStack 的 ItemMeta
     * @param key key 名
     * @param type 类型
     * @param value 值
     * @param <T> 类型 T
     */
    protected <T> void setNBT(@NotNull ItemMeta itemMeta, String key, PersistentDataType<T, T> type, T value) {
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, key), type, value);
    }
}
