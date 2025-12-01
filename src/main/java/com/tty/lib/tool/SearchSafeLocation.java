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
        long startTime = System.currentTimeMillis();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        int localX = x & 0xF;
        int localZ = z & 0xF;

        world.getChunkAtAsync(chunkX, chunkZ, true, true)
            .orTimeout(3, TimeUnit.SECONDS)
            .thenAccept(chunk ->
                Lib.Scheduler.runAtRegion(plugin, world, chunkX, chunkZ, task -> {
                    Integer safeY = findSafeY(world, chunk, localX, localZ);
                    if (safeY == null) {
                        future.complete(null);
                        return;
                    }
                    Location loc = new Location(world, x + 0.5, safeY, z + 0.5);
                    Log.debug("Found safe location at %s (took %sms)", loc, System.currentTimeMillis() - startTime);
                    future.complete(loc);
            }))
            .exceptionally(ex -> {
                Log.error("Chunk load error", ex);
                future.completeExceptionally(ex);
                return null;
            });

        return future;
    }

    private Integer findSafeY(World world, Chunk chunk, int x, int z) {

        int minY = world.getMinHeight();
        boolean isNether = world.getEnvironment() == World.Environment.NETHER;

        ChunkSnapshot snapshot = chunk.getChunkSnapshot(true, false, false, true);
        int startY = snapshot.getHighestBlockYAt(x, z);

        if (isNether) {
            while (startY > minY && chunk.getBlock(x, startY, z).getType() == Material.BEDROCK) {
                startY--;
            }
        }

        for (int y = startY; y >= minY; y--) {
            Block feet = chunk.getBlock(x, y, z);
            Block body = chunk.getBlock(x, y + 1, z);
            Block head = chunk.getBlock(x, y + 2, z);
            Block below = chunk.getBlock(x, y - 1, z);

            if (!isSafeStandingBlock(below.getType())) continue;
            if (isSolid(feet.getType()) || isSolid(body.getType()) || isSolid(head.getType())) continue;
            if (isDangerous(feet.getType()) || isDangerous(body.getType()) || isDangerous(head.getType())) continue;
            if (isNearDangerous(feet)) continue;

            return y;
        }

        return null;
    }

    /**
     * 四方向危险检测
     */
    private boolean isNearDangerous(Block block) {
        return isDangerous(block.getRelative(1, 0, 0).getType()) ||
                isDangerous(block.getRelative(-1, 0, 0).getType()) ||
                isDangerous(block.getRelative(0, 0, 1).getType()) ||
                isDangerous(block.getRelative(0, 0, -1).getType());
    }

    /**
     * 脚下可站立方块
     */
    private boolean isSafeStandingBlock(Material m) {
        return m.isSolid() &&
                !m.name().contains("LEAVES") &&
                !m.name().contains("GLASS") &&
                m != Material.SLIME_BLOCK &&
                m != Material.MAGMA_BLOCK &&
                m != Material.CACTUS &&
                m != Material.FIRE &&
                m != Material.SOUL_FIRE;
    }

    /**
     * 危险方块
     */
    private boolean isDangerous(Material m) {
        return switch (m) {
            case LAVA, FIRE, SOUL_FIRE, MAGMA_BLOCK, CACTUS, SWEET_BERRY_BUSH -> true;
            default -> false;
        };
    }

    /**
     * 是否固体方块
     */
    private boolean isSolid(Material m) {
        return m.isSolid();
    }
}
