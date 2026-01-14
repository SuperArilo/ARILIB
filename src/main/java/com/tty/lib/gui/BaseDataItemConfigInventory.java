package com.tty.lib.gui;

import com.tty.lib.dto.PageResult;
import com.tty.lib.entity.gui.BaseDataMenu;
import com.tty.lib.enum_type.GuiType;
import com.tty.lib.Lib;
import com.tty.lib.Log;
import com.tty.lib.tool.LibConfigUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class BaseDataItemConfigInventory<T> extends BaseConfigInventory {

    protected int pageNum = 1;
    protected final int pageSize;
    public final BaseDataMenu baseDataInstance;
    protected List<T> data;

    protected PageResult<T> lastPageResult = null;

    // 防止重复并发请求
    protected volatile boolean loading = false;

    public BaseDataItemConfigInventory(JavaPlugin plugin, BaseDataMenu baseDataInstance, Player player, GuiType type) {
        super(plugin, baseDataInstance, player, type);
        this.baseDataInstance = baseDataInstance;
        this.pageSize = baseDataInstance.getDataItems().getSlot().size();
    }

    /**
     * 上一页
     */
    public void prev() {
        if (this.loading) return; // 正在加载则忽略
        if (this.pageNum <= 1) {
            this.player.sendMessage(LibConfigUtils.t("base.page-change.none-prev"));
            return;
        }
        this.pageNum--;
        this.requestAndAccept(result -> {
            this.lastPageResult = result;
            this.data = result.getRecords();
            this.renderDataItem();
        });
    }

    /**
     * 下一页
     */
    public void next() {
        if (this.loading) return;
        if (this.lastPageResult != null && this.pageNum >= this.lastPageResult.getTotalPages()) {
            this.player.sendMessage(LibConfigUtils.t("base.page-change.none-next"));
            return;
        }

        this.pageNum++;
        this.requestAndAccept(result -> {
            if (result == null || (result.getTotalPages() > 0 && this.pageNum > result.getTotalPages())) {
                this.player.sendMessage(LibConfigUtils.t("base.page-change.none-next"));
                this.pageNum = Math.max(1, (int)Math.min(this.pageNum - 1, Math.max(1, result != null ? result.getTotalPages() : 1)));
                return;
            }

            this.lastPageResult = result;
            this.data = result.getRecords();
            this.renderDataItem();
        });
    }

    @Override
    protected void beforeOpen() {
        this.requestAndAccept(result -> {
            this.lastPageResult = result;
            this.data = result.getRecords();
            this.renderDataItem();
        });
    }

    /**
     * 请求数据的方法
     * @return 返回数据 CompletableFuture
     */
    protected abstract CompletableFuture<PageResult<T>> requestData();

    protected abstract Map<Integer, ItemStack> getRenderItem();

    private void requestAndAccept(Consumer<PageResult<T>> onSuccess) {
        CompletableFuture<PageResult<T>> future = this.requestData();
        if (future == null) {
            // 返回空页的标准 PageResult（避免 NullPointer）
            PageResult<T> empty = PageResult.build(List.of(), 0, 0, this.pageNum);
            this.lastPageResult = empty;
            onSuccess.accept(empty);
            return;
        }

        this.loading = true;
        future.thenAccept(result -> {
            try {
                // 先保存分页信息
                if (result != null) this.lastPageResult = result;
                onSuccess.accept(result != null ? result : PageResult.build(List.of(), 0, 0, this.pageNum));
            } catch (Exception e) {
                Log.error(e, "%s: processing request result error!", this.type.name());
            } finally {
                this.loading = false;
            }
        }).exceptionally(ex -> {
            Log.error(ex, "%s: request data error!", this.type.name());
            this.loading = false;
            return null;
        });
    }

    private void renderDataItem() {
        if (this.inventory == null) return;
        long l = System.currentTimeMillis();
        Map<Integer, ItemStack> renderItem;
        try {
            renderItem = this.getRenderItem();
        } catch (Exception e) {
            Log.error("get render item error.", e);
            return;
        }
        if (renderItem == null || renderItem.isEmpty()) return;

        for (Integer index : this.baseDataInstance.getDataItems().getSlot()) {
            Lib.Scheduler.runAtEntity(
                    this.plugin,
                    this.player,
                    i -> {
                        if (this.inventory == null) return;
                        this.inventory.clear(index);
                        this.inventory.setItem(index, renderItem.get(index));
                    },
                    null);
        }
        Log.debug("%s: submit render task time: %sms", this.type.name(), (System.currentTimeMillis() - l));
    }

    /**
     * 根据材质字符串来创建对于的 ItemStack
     * @param showMaterial 材质字符串
     * @return ItemStack 如果不存在则是 null
     */
    protected ItemStack createItemStack(@Nullable String showMaterial) {
        if (showMaterial == null) return null;
        ItemStack itemStack;
        try {
            itemStack = ItemStack.of(Material.valueOf(showMaterial.toUpperCase()));
            return itemStack;
        } catch (Exception e) {
            Log.error(e, "create ItemStack error. material %s", showMaterial);
            return null;
        }

    }

    /**
     * 将指定的 ItemStack 的 ItemMeta 设置为高亮模式
     * @param itemMeta ItemStack 的 ItemMeta
     */
    protected void setHighlight(@NotNull ItemMeta itemMeta) {
        itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    @Override
    public void clean() {
        super.clean();
        this.data = null;
        this.lastPageResult = null;
    }

}
