package io.github.coredex.forcegl20.AdaptiveRenderScaling;

import io.github.coredex.forcegl20.config.ForceGL20Config;
import net.minecraft.client.MinecraftClient;

public class PerformanceMonitor {
    private static final int FPS_UPDATE_INTERVAL_MS = ForceGL20Config.CONFIG.instance().updateInterval;
    private static final int FPS_CHECK_INTERVAL_MS = ForceGL20Config.CONFIG.instance().checkInterval;
    public static final int MIN_FPS_THRESHOLD = ForceGL20Config.CONFIG.instance().minFpsThreshold;
    public static final int MAX_FPS_THRESHOLD = ForceGL20Config.CONFIG.instance().maxFpsThreshold;

    private long lastUpdateTime;
    private long lastCheckTime;
    private int avgFps = 0;
    private int intervalCount = 0;

    public void tick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCheckTime >= FPS_CHECK_INTERVAL_MS) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                avgFps = avgFps + client.getCurrentFps();
                intervalCount++;
                if (currentTime - lastUpdateTime >= FPS_UPDATE_INTERVAL_MS){
                    avgFps = Math.floorDiv(avgFps, intervalCount);
                    AdaptiveChunkScaling.adjustRenderDistance(avgFps);
                    avgFps = 0;
                    intervalCount = 0;
                    lastUpdateTime = currentTime;
                }
            }
            lastCheckTime = currentTime;
        }
    }

    public int getCurrentFps() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getCurrentFps();
    }
}