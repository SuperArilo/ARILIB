package com.tty.lib.tool;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

public class ComponentUtils {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    public static TextComponent text(String content) {
        return build(null, content, null);
    }

    public static TextComponent text(String content, Map<String, Component> placeholders) {
        return build(null, content, placeholders);
    }

    public static TextComponent text(String content, Player player) {
        return build(player, content, null);
    }

    public static TextComponent text(String content, Player player, Map<String, Component> placeholders) {
        return build(player, content, placeholders);
    }

    public static Component textList(List<String> list, Map<String, Component> placeholders) {
        return Component.join(JoinConfiguration.separator(Component.newline()), list.stream().map(s -> text(s, placeholders)).toList());
    }

    public static Component textList(List<String> list) {
        return textList(list, null);
    }

    /**
     * 设置MC的全屏通知效果(Title
     * @param title 显示的title文本
     * @param subTitle 显示的副标题文本
     * @param fadeIn 进入动画时间 毫秒
     * @param stay 停留时间 毫秒
     * @param fadeOut 退出动画时间 毫秒
     * @return 返沪一个为Player设置的Title对象
     */
    public static Title setPlayerTitle(@NotNull String title, @NotNull String subTitle, long fadeIn, long stay, long fadeOut) {
        return Title.title(
                text(title),
                text(subTitle),
                Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))
        );
    }

    public static Title setPlayerTitle(@NotNull String title, @NotNull String subTitle, Map<String, Component> placeholders, long fadeIn, long stay, long fadeOut) {
        return Title.title(
                text(title, placeholders),
                text(subTitle, placeholders),
                Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))
        );
    }

    public static Component setClickEventText(Component component, ClickEvent.Action action, String actionText) {
        return component.clickEvent(ClickEvent.clickEvent(action, actionText));
    }

    public static TextComponent setClickEventText(String content, ClickEvent.Action action, String actionText) {
        return text(content).clickEvent(ClickEvent.clickEvent(action, actionText));
    }

    public static TextComponent setClickEventText(String content, Map<String, Component> placeholders, ClickEvent.Action action, String actionText) {
        return text(content, placeholders).clickEvent(ClickEvent.clickEvent(action, actionText));
    }

    public static TextComponent setHoverText(String content, String showText) {
        return text(content).hoverEvent(HoverEvent.showText(text(showText)));
    }

    public static Component setHoverItemText(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) return Component.empty();
        return itemStack.displayName().hoverEvent(itemStack.asHoverEvent(showItem -> showItem));
    }

    public static Component setEntityHoverText(Entity entity) {
        if (entity == null) return Component.empty();
        Component text = Component.text(entity.getType().key().asString());
        return Component.empty().append(entity.name()).hoverEvent(HoverEvent.showText(text));
    }

    @SuppressWarnings("PatternValidation")
    private static TextComponent build(Player player, String template, Map<String, Component> placeholders) {
        Objects.requireNonNull(template, "template cannot be null");
        if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            template = PlaceholderAPI.setPlaceholders(player, template);
        }
        template = ColorConverterLegacy.convert(template);
        TagResolver resolver;
        if (placeholders == null || placeholders.isEmpty()) {
            resolver = TagResolver.empty();
        } else {
            TagResolver.Builder builder = TagResolver.builder();
            for (Map.Entry<String, Component> e : placeholders.entrySet()) {
                String key = e.getKey();
                if (key == null) continue;
                Component value = e.getValue();
                builder.tag(key, Tag.selfClosingInserting(Objects.requireNonNullElseGet(value, Component::empty)));
            }
            resolver = builder.build();
        }

        Component comp = MM.deserialize(template, resolver);
        if (comp instanceof TextComponent tc) {
            return tc.decoration(TextDecoration.ITALIC, false);
        }
        return Component.empty().append(comp.decoration(TextDecoration.ITALIC, false));
    }


}
