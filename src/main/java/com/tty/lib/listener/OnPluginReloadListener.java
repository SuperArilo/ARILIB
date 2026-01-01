package com.tty.lib.listener;

import com.tty.lib.Lib;
import com.tty.lib.Log;
import com.tty.lib.dto.event.CustomPluginReloadEvent;
import com.tty.lib.tool.LibConfigUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPluginReloadListener implements Listener {

    @EventHandler
    public void pluginReload(CustomPluginReloadEvent event) {
        Lib.reloadAllConfig();
        Log.init(Lib.instance.getLogger(), Lib.DEBUG);
        event.getSender().sendMessage(LibConfigUtils.t("function.reload.success"));
    }

}
