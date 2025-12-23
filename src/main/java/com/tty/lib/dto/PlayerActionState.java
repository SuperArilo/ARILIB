package com.tty.lib.dto;

import com.tty.lib.Lib;
import com.tty.lib.Log;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerActionState extends State {

    @Getter
    @Setter
    private Entity tool_entity;

    public PlayerActionState(Entity owner) {
        super(owner, Integer.MAX_VALUE);
    }

    public void removeToolEntity(JavaPlugin plugin) {
        if (this.tool_entity == null) return;
        Lib.Scheduler.runAtEntity(plugin,
            this.tool_entity,
            i-> {
                this.getOwner().eject();
                this.tool_entity.remove();
                this.tool_entity = null;
                this.setOver(true);
                Log.debug("player %s ejected, remove tool entity", this.getOwner().getName());
            },
            () -> Log.error("remove tool_entity error."));
    }

}
