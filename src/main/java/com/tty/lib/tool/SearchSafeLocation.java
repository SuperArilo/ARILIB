package com.tty.lib.tool;

import com.tty.lib.Lib;
import com.tty.lib.Log;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public record SearchSafeLocation(JavaPlugin plugin) {

    public CompletableFuture<Location> search(World world, int x, int z) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        final long l = System.currentTimeMillis();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        int relativeX = x & 0xF;
        int relativeZ = z & 0xF;

        boolean isNether = world.getEnvironment().equals(World.Environment.NETHER);

        world.getChunkAtAsync(chunkX, chunkZ)
            .orTimeout(3, TimeUnit.SECONDS)
            .thenAccept(chunk -> {
                int highestBlockYAt = isNether ?
                        this.getSafeNetherY(world, chunk, relativeX, relativeZ) :
                        chunk.getChunkSnapshot().getHighestBlockYAt(relativeX, relativeZ);
                if (highestBlockYAt == -1) {
                    future.complete(null);
                    return;
                }
                Lib.Scheduler.runAtRegion(plugin, world, chunkX, chunkZ, i -> {
                    if (this.isLocationSafe(chunk, relativeX, highestBlockYAt, relativeZ)) {
                        Log.debug("random location x: %s, y: %s, z: %s, search time: %sms", x, highestBlockYAt, z, (System.currentTimeMillis() - l));
                        future.complete(new Location(world, x + 0.5, highestBlockYAt + 1, z + 0.5));
                    } else {
                        future.complete(null);
                    }
            });
        }).exceptionally(i -> {
            future.completeExceptionally(i);
            return null;
        });
        return future;
    }

    //下界特殊处理
    public int getSafeNetherY(World world, Chunk chunk, int localX, int localZ) {
        final int minHeight = world.getMinHeight();
        final int maxHeight = world.getMaxHeight();

        for (int y = maxHeight - 1; y >= minHeight; y--) {
            Block block = chunk.getBlock(localX, y, localZ);
            Material type = block.getType();

            if (!type.isSolid() || block.isPassable()) continue;

            if (type == Material.LAVA || type == Material.WATER) continue;

            if (type == Material.BEDROCK) continue;

            Block above1 = chunk.getBlock(localX, y + 1, localZ);
            Block above2 = chunk.getBlock(localX, y + 2, localZ);

            if ((above1.isPassable() || !above1.getType().isSolid()) &&
                    (above2.isPassable() || !above2.getType().isSolid())) {
                return y;
            }
        }
        return -1;
    }

    private boolean isLocationSafe(Chunk chunk, int chunkX, int chunkY, int chunkZ) {

        //判断Y轴高度合不合法
        if (chunkY < chunk.getWorld().getMinHeight()) {
            Log.debug("rtp: illegal Y-axis height.");
            return false;
        }

        Block block = chunk.getBlock(chunkX, chunkY, chunkZ);

        //身体检查
        Material head = chunk.getBlock(chunkX, chunkY + 2, chunkZ).getType();
        Material body = chunk.getBlock(chunkX, chunkY + 1, chunkZ).getType();
        Material feet = block.getType();

        //周围检查
        Material left = block.getRelative(1, 0, 0).getType();
        Material right = block.getRelative(-1, 0, 0).getType();
        Material front = block.getRelative(0, 1, 0).getType();
        Material behind = block.getRelative(0, -1, 0).getType();

        if (!isSafeStandingBlock(feet)) {
            Log.debug("standing block illegal.");
            return false;
        }

        if (isSolid(body) || isSolid(head) ||
                isDangerous(body) || isDangerous(head) ||
                isDangerous(left) || isDangerous(right) || isDangerous(front) || isDangerous(behind)) {
            Log.debug("the blocks around the player are illegal.");
            return false;
        }

        if (isDangerous(feet)) {
            Log.debug("feet block is dangerous.");
            return false;
        }
        if (chunk.getBlock(chunkX, chunkY - 1, chunkZ).getType().isAir()) {
            Log.debug("feet block illegal.");
            return false;
        }

        return true;

    }

    private boolean isSafeStandingBlock(Material material) {
        return material.isSolid() &&
                !material.name().contains("LEAVES") &&
                !material.name().contains("GLASS") &&
                material != Material.SLIME_BLOCK;
    }

    private boolean isSolid(Material material) {
        return switch (material) {
            case AIR, CAVE_AIR, VOID_AIR, WATER, LAVA -> false;
            default -> material.isSolid();
        };
    }

    private boolean isDangerous(Material material) {
        return switch (material) {
            case LAVA, FIRE, SOUL_FIRE, MAGMA_BLOCK, CACTUS, SWEET_BERRY_BUSH -> true;
            default -> false;
        };
    }
}