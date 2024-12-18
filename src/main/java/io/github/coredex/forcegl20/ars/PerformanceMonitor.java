package io.github.coredex.forcegl20.ars;

import io.github.coredex.forcegl20.config.ForceGL20Config;
import net.minecraft.client.MinecraftClient;

public class PerformanceMonitor {
    private static final int FPS_UPDATE_INTERVAL_MS = ForceGL20Config.CONFIG.instance().checkInterval;
    public static final int MIN_FPS_THRESHOLD = ForceGL20Config.CONFIG.instance().minFpsThreshold;
    public static final int MAX_FPS_THRESHOLD = ForceGL20Config.CONFIG.instance().maxFpsThreshold;

    private long lastUpdateTime;
    private int currentFps;

    public void tick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= FPS_UPDATE_INTERVAL_MS) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                currentFps = client.getCurrentFps();
                AdaptiveRenderScaling.adjustRenderDistance(currentFps);
            }
            lastUpdateTime = currentTime;
        }
    }

    public int getCurrentFps() {
        return currentFps;
    }
}
