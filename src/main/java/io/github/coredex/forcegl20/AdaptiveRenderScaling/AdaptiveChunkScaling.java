package io.github.coredex.forcegl20.AdaptiveRenderScaling;

import io.github.coredex.forcegl20.config.ForceGL20Config;
import net.minecraft.client.MinecraftClient;

public class AdaptiveChunkScaling {
    public static int MIN_RENDER_DISTANCE = ForceGL20Config.CONFIG.instance().minRenderDistance;  // Minimum render distance
    public static int MAX_RENDER_DISTANCE = ForceGL20Config.CONFIG.instance().maxRenderDistance; // Maximum render distance

    private static int lastRenderDistance = ForceGL20Config.CONFIG.instance().defaultRenderDistance; // Default render distance

    public static void setDefaultRenderDistance(){
        RenderScalingTools.setRenderDistance(ForceGL20Config.CONFIG.instance().defaultRenderDistance);
    }

    public static void adjustRenderDistance(int fps) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        int newRenderDistance = lastRenderDistance;

        if (fps < PerformanceMonitor.MIN_FPS_THRESHOLD - 10) {
            newRenderDistance = MIN_RENDER_DISTANCE;
        } else if (fps < PerformanceMonitor.MIN_FPS_THRESHOLD) {
            newRenderDistance = Math.max(MIN_RENDER_DISTANCE, lastRenderDistance - 1);
        } else if (fps > PerformanceMonitor.MAX_FPS_THRESHOLD + 15) {
            newRenderDistance = MAX_RENDER_DISTANCE;
        } else if (fps > PerformanceMonitor.MAX_FPS_THRESHOLD) {
            newRenderDistance = Math.min(MAX_RENDER_DISTANCE, lastRenderDistance + 3);
        }

        if (newRenderDistance != lastRenderDistance) {
            RenderScalingTools.setRenderDistance(newRenderDistance);
            lastRenderDistance = newRenderDistance;
        }
    }

}
