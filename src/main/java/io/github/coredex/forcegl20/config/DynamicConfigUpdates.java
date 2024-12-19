package io.github.coredex.forcegl20.config;

import io.github.coredex.forcegl20.AdaptiveRenderScaling.AdaptiveChunkScaling;
import io.github.coredex.forcegl20.AdaptiveRenderScaling.PerformanceMonitor;
import io.github.coredex.forcegl20.ForceGL20;
public class DynamicConfigUpdates {
    public static void applyDynamicChanges() {
        if (ForceGL20Config.CONFIG.instance().adaptiveRenderScalingEnabled) {
            PerformanceMonitor.MIN_FPS_THRESHOLD = ForceGL20Config.CONFIG.instance().minFpsThreshold;
            PerformanceMonitor.MAX_FPS_THRESHOLD = ForceGL20Config.CONFIG.instance().maxFpsThreshold;
            PerformanceMonitor.FPS_UPDATE_INTERVAL_MS = ForceGL20Config.CONFIG.instance().updateInterval;
            PerformanceMonitor.FPS_CHECK_INTERVAL_MS = ForceGL20Config.CONFIG.instance().checkInterval;
            AdaptiveChunkScaling.MAX_RENDER_DISTANCE = ForceGL20Config.CONFIG.instance().maxRenderDistance;
            AdaptiveChunkScaling.MIN_RENDER_DISTANCE = ForceGL20Config.CONFIG.instance().minRenderDistance;
        } else if (ForceGLOptionsScreen.ARScalingEnabled && !ForceGL20Config.CONFIG.instance().adaptiveRenderScalingEnabled){
            ForceGLOptionsScreen.ARScalingEnabled = ForceGL20Config.CONFIG.instance().adaptiveRenderScalingEnabled;
            ForceGL20.isListenerActive = false;
        }
    }
}
