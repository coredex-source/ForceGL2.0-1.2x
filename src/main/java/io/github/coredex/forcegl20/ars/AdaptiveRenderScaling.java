package io.github.coredex.forcegl20.ars;

import io.github.coredex.forcegl20.config.ForceGL20Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.client.world.ClientWorld;

import java.util.HashSet;
import java.util.Set;

public class AdaptiveRenderScaling {
    private static final int MIN_RENDER_DISTANCE = ForceGL20Config.CONFIG.instance().minRenderDistance;  // Minimum render distance
    private static final int MAX_RENDER_DISTANCE = ForceGL20Config.CONFIG.instance().maxRenderDistance; // Maximum render distance

    private static int lastRenderDistance = ForceGL20Config.CONFIG.instance().maxRenderDistance; // Default render distance
    private static final Set<ChunkPos> loadedChunks = new HashSet<>();

    public static void adjustRenderDistance(int fps) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null || client.world == null) return;

        int newRenderDistance = lastRenderDistance;

        if (fps < PerformanceMonitor.MIN_FPS_THRESHOLD) {
            // Decrease render distance
            newRenderDistance = Math.max(MIN_RENDER_DISTANCE, lastRenderDistance - 1);
        } else if (fps > PerformanceMonitor.MAX_FPS_THRESHOLD) {
            // Increase render distance
            newRenderDistance = Math.min(MAX_RENDER_DISTANCE, lastRenderDistance + 3);
        }

        if (newRenderDistance != lastRenderDistance) {
            updateRenderDistance(client, client.world, lastRenderDistance, newRenderDistance);
            lastRenderDistance = newRenderDistance;
        }
    }

    private static void updateRenderDistance(MinecraftClient client, ClientWorld world, int oldDistance, int newDistance) {
        Set<ChunkPos> newChunks = calculateVisibleChunks(client, world, newDistance);

        // Load new chunks
        for (ChunkPos chunkPos : newChunks) {
            if (!loadedChunks.contains(chunkPos)) {
                world.getChunkManager().loadChunkFromPacket(chunkPos.x, chunkPos.z, null, null, null);
            }
        }

        // Unload chunks outside the new range
        for (ChunkPos chunkPos : loadedChunks) {
            if (!newChunks.contains(chunkPos)) {
                world.getChunkManager().unload(new net.minecraft.util.math.ChunkPos(chunkPos.x, chunkPos.z));
            }
        }

        loadedChunks.clear();
        loadedChunks.addAll(newChunks);
    }

    private static Set<ChunkPos> calculateVisibleChunks(MinecraftClient client, ClientWorld world, int renderDistance) {
        Set<ChunkPos> visibleChunks = new HashSet<>();

        int playerChunkX = client.player.getChunkPos().x >> 4;
        int playerChunkZ = client.player.getChunkPos().z >> 4;

        for (int dx = -renderDistance; dx <= renderDistance; dx++) {
            for (int dz = -renderDistance; dz <= renderDistance; dz++) {
                visibleChunks.add(new ChunkPos(playerChunkX + dx, playerChunkZ + dz));
            }
        }

        return visibleChunks;
    }
}