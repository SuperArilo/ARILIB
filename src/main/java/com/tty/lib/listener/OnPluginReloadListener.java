package com.tty.lib.listener;

import com.tty.api.event.WhenPluginConfigReloadCompleteEvent;
import com.tty.lib.Lib;
import com.tty.api.event.WhenPluginExecuteReloadCommandEvent;
import com.tty.lib.tool.LibConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPluginReloadListener implements Listener {

    @EventHandler
    public void pluginReload(WhenPluginExecuteReloadCommandEvent event) {
        if (!event.getPlugin().equals(Lib.instance)) return;
        Lib.instance.doReloadAllFiles(event.getSender());
    }

    @EventHandler
    public void configReload(WhenPluginConfigReloadCompleteEvent event) {
        if (!event.getPlugin().equals(Lib.instance)) return;
        Lib.PLACEHOLDER.setInstance(Lib.instance.getConfigInstance());
        CommandSender sender = event.getSender();
        if (sender != null) {
            sender.sendMessage(LibConfigUtils.t("function.reload.success"));
        }
    }

}
