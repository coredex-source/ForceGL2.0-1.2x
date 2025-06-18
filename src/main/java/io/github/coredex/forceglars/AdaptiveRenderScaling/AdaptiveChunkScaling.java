package io.github.coredex.forceglars.AdaptiveRenderScaling;

import io.github.coredex.forceglars.config.ForceGLARSConfig;
import net.minecraft.client.MinecraftClient;

public class AdaptiveChunkScaling {
    public static int MIN_RENDER_DISTANCE = ForceGLARSConfig.CONFIG.instance().minRenderDistance;  // Minimum render distance
    public static int MAX_RENDER_DISTANCE = ForceGLARSConfig.CONFIG.instance().maxRenderDistance; // Maximum render distance

    private static int lastRenderDistance = ForceGLARSConfig.CONFIG.instance().defaultRenderDistance; // Default render distance

    public static void setDefaultRenderDistance(){
        int defaultDistance = ForceGLARSConfig.CONFIG.instance().defaultRenderDistance;
        RenderScalingTools.setRenderDistance(defaultDistance);
        lastRenderDistance = defaultDistance;
    }

    public static void adjustRenderDistance(int fps) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        // Use current config values in case they changed
        int currentMinDistance = ForceGLARSConfig.CONFIG.instance().minRenderDistance;
        int currentMaxDistance = ForceGLARSConfig.CONFIG.instance().maxRenderDistance;
        int minFpsThreshold = ForceGLARSConfig.CONFIG.instance().minFpsThreshold;
        int maxFpsThreshold = ForceGLARSConfig.CONFIG.instance().maxFpsThreshold;

        int newRenderDistance = lastRenderDistance;

        if (fps < minFpsThreshold - 10) {
            newRenderDistance = currentMinDistance;
        } else if (fps < minFpsThreshold) {
            newRenderDistance = Math.max(currentMinDistance, lastRenderDistance - 1);
        } else if (fps > maxFpsThreshold + 15) {
            newRenderDistance = currentMaxDistance;
        } else if (fps > maxFpsThreshold) {
            newRenderDistance = Math.min(currentMaxDistance, lastRenderDistance + 3);
        }

        if (newRenderDistance != lastRenderDistance) {
            RenderScalingTools.setRenderDistance(newRenderDistance);
            lastRenderDistance = newRenderDistance;
        }
    }

}
