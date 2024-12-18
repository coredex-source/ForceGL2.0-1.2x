package io.github.coredex.forcegl20.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("ForceGL");
    @Inject(method = "updateLoadDistance", at = @At("HEAD"), cancellable = true)
    private void onSetRenderDistance(int renderDistance, CallbackInfo ci) {
        LOGGER.warn("loaded");
        ci.cancel(); // Cancel default chunk reload behavior

        // Get Minecraft client instance
        MinecraftClient client = MinecraftClient.getInstance();

        // Calculate affected chunks
        int oldDistance = client.options.getViewDistance().getValue();
        if (renderDistance == oldDistance) {
            return; // No change in render distance, no action needed
        }

        ClientChunkManager chunkManager = client.world.getChunkManager();

        if (renderDistance > oldDistance) {
            // Load new chunks at the perimeter
            loadNewChunks(chunkManager, oldDistance, renderDistance);
        } else {
            // Unload chunks outside the new bounds
            unloadExtraChunks(chunkManager, oldDistance, renderDistance);
        }
    }

    private void loadNewChunks(ClientChunkManager chunkManager, int oldDistance, int newDistance) {
        LOGGER.warn("loadedNC");
        MinecraftClient client = MinecraftClient.getInstance();
        int playerChunkX = client.player.getChunkPos().x;
        int playerChunkZ = client.player.getChunkPos().z;

        for (int x = -newDistance; x <= newDistance; x++) {
            for (int z = -newDistance; z <= newDistance; z++) {
                // Check if the chunk is outside the old render distance but within the new one
                if (Math.max(Math.abs(x), Math.abs(z)) > oldDistance) {
                    ChunkPos chunkPos = new ChunkPos(playerChunkX + x, playerChunkZ + z);
                    chunkManager.loadChunkFromPacket(chunkPos.x, chunkPos.z, null, null, null); // Load the chunk
                }
            }
        }
    }

    private void unloadExtraChunks(ClientChunkManager chunkManager, int oldDistance, int newDistance) {
        LOGGER.warn("UloadedEC");
        MinecraftClient client = MinecraftClient.getInstance();
        int playerChunkX = client.player.getChunkPos().x;
        int playerChunkZ = client.player.getChunkPos().z;

        for (int x = -oldDistance; x <= oldDistance; x++) {
            for (int z = -oldDistance; z <= oldDistance; z++) {
                // Check if the chunk is within the old render distance but outside the new one
                if (Math.max(Math.abs(x), Math.abs(z)) > newDistance) {
                    ChunkPos chunkPos = new ChunkPos(playerChunkX + x, playerChunkZ + z);
                    chunkManager.unload(chunkPos); // Unload the chunk
                }
            }
        }
    }
}
