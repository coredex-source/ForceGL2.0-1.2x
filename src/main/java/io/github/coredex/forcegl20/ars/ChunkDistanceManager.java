package io.github.coredex.forcegl20.ars;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.ChunkPos;

public class ChunkDistanceManager {

    public static void updateRenderDistance(int newRenderDistance, int oldRenderDistance) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.player == null) return;

        ClientChunkManager chunkManager = client.world.getChunkManager();

        if (newRenderDistance > oldRenderDistance) {
            // Load new chunks incrementally
            loadNewChunks(chunkManager, oldRenderDistance, newRenderDistance, client);
        } else {
            // Unload extra chunks
            unloadExtraChunks(chunkManager, oldRenderDistance, newRenderDistance, client);
        }
    }

    private static void loadNewChunks(ClientChunkManager chunkManager, int oldDistance, int newDistance, MinecraftClient client) {
        int playerChunkX = client.player.getChunkPos().x;
        int playerChunkZ = client.player.getChunkPos().z;

        for (int x = -newDistance; x <= newDistance; x++) {
            for (int z = -newDistance; z <= newDistance; z++) {
                int maxDist = Math.max(Math.abs(x), Math.abs(z));
                if (maxDist > oldDistance && maxDist <= newDistance) {
                    ChunkPos chunkPos = new ChunkPos(playerChunkX + x, playerChunkZ + z);
                    chunkManager.loadChunkFromPacket(chunkPos.x, chunkPos.z, null, null, null);
                }
            }
        }
    }

    private static void unloadExtraChunks(ClientChunkManager chunkManager, int oldDistance, int newDistance, MinecraftClient client) {
        int playerChunkX = client.player.getChunkPos().x;
        int playerChunkZ = client.player.getChunkPos().z;

        for (int x = -oldDistance; x <= oldDistance; x++) {
            for (int z = -oldDistance; z <= oldDistance; z++) {
                int maxDist = Math.max(Math.abs(x), Math.abs(z));
                if (maxDist > newDistance) {
                    ChunkPos chunkPos = new ChunkPos(playerChunkX + x, playerChunkZ + z);
                    chunkManager.unload(chunkPos);
                }
            }
        }
    }
}
