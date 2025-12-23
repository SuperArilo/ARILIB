package com.tty.lib.dto;

import com.tty.lib.Lib;
import com.tty.lib.Log;
import com.tty.lib.tool.FormatUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class PlayerActionState extends State {

    @Getter
    @Setter
    private Entity tool_entity;

    public PlayerActionState(Entity owner) {
        super(owner, Integer.MAX_VALUE);
    }

    public void createToolEntity(@NotNull World world, @NotNull Location location, Consumer<AreaEffectCloud> i) {
        if (this.tool_entity != null) {
            Location l = tool_entity.getLocation();
            Log.error("tool_entity already exists. location %s", FormatUtils.XYZText(l.getX(), l.getY(), l.getZ()));
            Log.warn("removing...");
            this.tool_entity.remove();
            this.tool_entity = null;
        }
        this.tool_entity = world.spawnEntity(
            location,
            EntityType.AREA_EFFECT_CLOUD,
            CreatureSpawnEvent.SpawnReason.CUSTOM,
            entity -> {
                if (entity instanceof AreaEffectCloud cloud) {
                    cloud.setRadius(0);
                    cloud.setInvulnerable(true);
                    cloud.setGravity(false);
                    cloud.setInvisible(true);
                    cloud.setParticle(Particle.DUST, new Particle.DustOptions(Color.fromRGB(0, 0, 0), 0f));
                    i.accept(cloud);
                }
            }
        );
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
