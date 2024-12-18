package io.github.coredex.forcegl20.ars;

import io.github.coredex.forcegl20.config.ForceGL20Config;
import net.minecraft.client.MinecraftClient;

public class AdaptiveRenderScaling {
    private static final int MIN_RENDER_DISTANCE = ForceGL20Config.CONFIG.instance().minRenderDistance;  // Minimum render distance
    private static final int MAX_RENDER_DISTANCE = ForceGL20Config.CONFIG.instance().maxRenderDistance; // Maximum render distance

    private static int lastRenderDistance = ForceGL20Config.CONFIG.instance().maxRenderDistance; // Default render distance

    public static void adjustRenderDistance(int fps) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        int newRenderDistance = lastRenderDistance;

        if (fps < PerformanceMonitor.MIN_FPS_THRESHOLD) {
            // Decrease render distance
            newRenderDistance = Math.max(MIN_RENDER_DISTANCE, lastRenderDistance - 1);
        } else if (fps > PerformanceMonitor.MAX_FPS_THRESHOLD) {
            // Increase render distance
            newRenderDistance = Math.min(MAX_RENDER_DISTANCE, lastRenderDistance + 3);
        }

        if (newRenderDistance != lastRenderDistance) {
            client.options.setServerViewDistance(newRenderDistance);
            client.options.getViewDistance().setValue(newRenderDistance);
            lastRenderDistance = newRenderDistance;
        }
    }
}
